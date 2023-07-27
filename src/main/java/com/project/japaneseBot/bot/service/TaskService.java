package com.project.japaneseBot.bot.service;

import com.project.japaneseBot.alphabet.model.entity.AlphabetsEntity;
import com.project.japaneseBot.alphabet.model.enums.AlphabetsTypes;
import com.project.japaneseBot.alphabet.repository.AlphabetsRepository;
import com.project.japaneseBot.task.model.dto.LetterDTO;
import com.project.japaneseBot.task.model.entity.TaskEntity;
import com.project.japaneseBot.task.model.entity.TaskLettersEntity;
import com.project.japaneseBot.task.model.entity.TaskSettingsEntity;
import com.project.japaneseBot.task.repository.TaskRepository;
import com.project.japaneseBot.user.model.entity.UserEntity;
import com.project.japaneseBot.user.model.enums.UserMode;
import com.project.japaneseBot.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class TaskService {
    UserRepository userRepository;
    TaskRepository taskRepository;
    AlphabetsRepository alphabetsRepository;
    public String checkAnswer(long userId, String receivedMessage) {
        TaskEntity task = taskRepository.findFirstByUserEntity_UserIdOrderByTaskIdDesc(userId)
                .orElseThrow(RuntimeException::new);
        String returnText = "Error";
        if (task.getQuestionNumber() <= task.getQuestionCount()) {
            TaskLettersEntity taskLetters = task.getLetters().get(task.getQuestionNumber() - 1);
            String letterKey = taskLetters.getLetterKey();
            String letterAlphabet = taskLetters.getLetterAlphabet();
            String letterValue = getLetterValue(letterKey, letterAlphabet);
            boolean isCorrect;
            if (receivedMessage.equals(letterValue)) {
                returnText = "Correct";
                isCorrect = true;
            } else {
                returnText = "Wrong";
                isCorrect = false;
            }
            updateTaskProgress(task, isCorrect);
            returnText = processTaskResult(task, returnText, userId);
        }
        return returnText;
    }

    private String processTaskResult(TaskEntity task, String returnText, long userId) {
        if (task.getQuestionNumber() > task.getQuestionCount()) {
            returnText = returnText + "\n\nYour answers: " + task.getIsAnswerCorrect().toString();
            closeTask(userId);
        } else {
            String letterValue = task.getLetters().get(task.getQuestionNumber() - 1).getLetterKey();
            returnText = returnText + "\n\nLetter - " + letterValue + "\nPronouns?";
        }
        return returnText;
    }

    private void updateTaskProgress(TaskEntity task, boolean answerResult) {
        List<Boolean> answers = task.getIsAnswerCorrect();
        answers.add(answerResult);
        int currentNumber = task.getQuestionNumber();
        task.setQuestionNumber(currentNumber + 1);
        taskRepository.save(task);
    }

    private String getLetterValue(String letterKey, String letterAlphabet) {
        if (alphabetsRepository.existsByLetter(letterKey)) {
            return alphabetsRepository.findByLetter(letterKey).orElseThrow(RuntimeException::new)
                    .getLetterPronouns();
        } else if (alphabetsRepository.existsByLetterPronouns(letterKey)) {
            return alphabetsRepository.findByAlphabetAndLetterPronounsIgnoreCase(letterAlphabet, letterKey)
                    .orElseThrow(RuntimeException::new)
                    .getLetter();
        } else {
            throw new RuntimeException("Value " + letterKey + " does`t exist in DataBase" );
        }
    }

    public void closeTask(long userId) {
        UserEntity user = userRepository.findByUserId(userId);
        user.setMode(UserMode.TEXT_MODE.name());
        userRepository.save(user);
    }

    @Transactional
    public String initialiseTask(long userId, TaskSettingsEntity settings) {
        UserEntity user = userRepository.findByUserId(userId);
        TaskEntity task = createAndSaveTask(user, settings);
        user.setMode(UserMode.TASK_MODE.name());
        userRepository.save(user);
        var letter = task.getLetters().get(task.getQuestionNumber() - 1).getLetterKey();
        return "Letter - " + letter + "\nPronouns?";
    }

    @Transactional
    public TaskEntity createAndSaveTask(UserEntity user, TaskSettingsEntity settings) {
        TaskEntity task = TaskEntity.builder()
                .questionNumber(1)
                .questionCount(settings.getQuestionCount())
                .isAnswerCorrect(new LinkedList<>())
                .userEntity(user)
                .taskSettingsEntity(settings)
                .build();
        task.setLetters(createTaskLettersEntities(settings, task));
        taskRepository.save(task);
        return task;
    }

    private List<TaskLettersEntity> createTaskLettersEntities(TaskSettingsEntity settings, TaskEntity task) {
        List<TaskLettersEntity> taskLettersEntities = new ArrayList<>();
        List<LetterDTO> lettersList = generateLettersList(settings);
        for (LetterDTO letterDTO : lettersList) {
            taskLettersEntities.add(TaskLettersEntity.builder()
                    .letterKey(letterDTO.letterKey())
                    .letterValue(letterDTO.letterValue())
                    .letterAlphabet(letterDTO.letterAlphabet())
                    .taskEntity(task)
                    .build());
        }
        return taskLettersEntities;
    }

    private List<LetterDTO> generateLettersList(TaskSettingsEntity settings) {
        List<LetterDTO> lettersList = new LinkedList<>();
        int count = settings.getQuestionCount();
        List<AlphabetsEntity> alphabets = getAlphabet(settings);
        if (alphabets.isEmpty()) {
            throw new RuntimeException("Alphabet still empty after getAlphabet() method");
        }
        for (int i = 0; i < count; i++) {
            LetterDTO letterDTO = getLetterDTO(settings, alphabets);
            lettersList.add(letterDTO);
        }
        if (lettersList.isEmpty()) {
            throw new RuntimeException("lettersList still empty after getLetterDTO() method");
        }
        return lettersList;
    }

    private List<AlphabetsEntity> getAlphabet(TaskSettingsEntity settings) {
        if (settings.getAlphabet().equals(AlphabetsTypes.ALL.name())) {
            return alphabetsRepository.findAll();
        }
        return alphabetsRepository.findByAlphabet(settings.getAlphabet());
    }

    private LetterDTO getLetterDTO(TaskSettingsEntity settings, List<AlphabetsEntity> alphabets) {
        Random random = new Random();
        int alphabetsLength = alphabets.size();
        AlphabetsEntity alphabetsEntity = alphabets.get(random.nextInt(alphabetsLength));
        String letterKey;
        String letterValue;
        boolean useLetter;
        if (settings.getUseLetters() && settings.getUsePronouns()) {
            useLetter = random.nextBoolean();
        } else {
            useLetter = settings.getUseLetters();
        }
        if (useLetter) {
            letterKey = alphabetsEntity.getLetter();
            letterValue = alphabetsEntity.getLetterPronouns();
        } else {
            letterKey = alphabetsEntity.getLetterPronouns();
            letterValue = alphabetsEntity.getLetter();
        }
        return LetterDTO.builder()
                .letterKey(letterKey)
                .letterValue(letterValue)
                .letterAlphabet(alphabetsEntity.getAlphabet())
                .build();
    }
}