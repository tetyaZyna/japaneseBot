package com.project.japaneseBot.bot.service;

import com.project.japaneseBot.alphabet.model.entity.AlphabetsEntity;
import com.project.japaneseBot.alphabet.model.enums.AlphabetsTypes;
import com.project.japaneseBot.alphabet.repository.AlphabetsRepository;
import com.project.japaneseBot.task.model.dto.LetterDTO;
import com.project.japaneseBot.task.model.entity.TaskEntity;
import com.project.japaneseBot.task.model.entity.TaskLettersEntity;
import com.project.japaneseBot.task.model.entity.TaskSettingsEntity;
import com.project.japaneseBot.alphabet.model.enums.AlphabetRepresentation;
import com.project.japaneseBot.task.repository.TaskRepository;
import com.project.japaneseBot.user.model.entity.UserEntity;
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
    UserService userService;

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
            returnText = returnText + "\n\nYour answers: " + visualiseAnswers(task);
            userService.startTextMode(userId);
        } else {
            TaskLettersEntity letter = task.getLetters().get(task.getQuestionNumber() - 1);
            String letterValue = letter.getLetterKey();
            returnText = returnText + "\n\nWhich is equivalent to - " + letterValue + " ";
            if (letter.getKeyType().equals(AlphabetRepresentation.PRONOUNS.name())) {
                returnText = returnText + "(" + letter.getLetterAlphabet().toLowerCase() + ")";
            }
            returnText = returnText + "\nYour answer?";
        }
        return returnText;
    }

    private String  visualiseAnswers(TaskEntity task) {
        List<Boolean> correctnessList = task.getIsAnswerCorrect();
        StringBuilder stringBuilder = new StringBuilder();
        for (boolean answer : correctnessList) {
            if (answer) {
                stringBuilder.append("\uD83D\uDFE2");
            } else {
                stringBuilder.append("\uD83D\uDD34");
            }
        }
        return stringBuilder.toString();
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

    @Transactional
    public String initialiseTask(long userId, TaskSettingsEntity settings) {
        UserEntity user = userRepository.findByUserId(userId);
        TaskEntity task = createAndSaveTask(user, settings);
        userService.startTaskMode(userId);
        return processTaskResult(task, "", userId);
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
                    .keyType(letterDTO.keyType())
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
        String keyType;
        boolean useLetter;
        if (settings.getUseLetters() && settings.getUsePronouns()) {
            useLetter = random.nextBoolean();
        } else {
            useLetter = settings.getUseLetters();
        }
        if (useLetter) {
            letterKey = alphabetsEntity.getLetter();
            keyType = AlphabetRepresentation.LETTER.name();
            letterValue = alphabetsEntity.getLetterPronouns();
        } else {
            letterKey = alphabetsEntity.getLetterPronouns();
            keyType = AlphabetRepresentation.PRONOUNS.name();
            letterValue = alphabetsEntity.getLetter();
        }
        return LetterDTO.builder()
                .letterKey(letterKey)
                .keyType(keyType)
                .letterValue(letterValue)
                .letterAlphabet(alphabetsEntity.getAlphabet())
                .build();
    }
}