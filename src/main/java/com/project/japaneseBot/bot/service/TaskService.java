package com.project.japaneseBot.bot.service;

import com.project.japaneseBot.alphabet.repository.ReadOnlyHiraganaRepository;
import com.project.japaneseBot.alphabet.repository.ReadOnlyKatakanaRepository;
import com.project.japaneseBot.bot.model.UserMode;
import com.project.japaneseBot.task.entity.TaskEntity;
import com.project.japaneseBot.task.entity.TaskLettersEntity;
import com.project.japaneseBot.task.entity.TaskSettingsEntity;
import com.project.japaneseBot.task.repository.TaskRepository;
import com.project.japaneseBot.user.entity.UserEntity;
import com.project.japaneseBot.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class TaskService {
    UserRepository userRepository;
    ReadOnlyKatakanaRepository katakanaRepository;
    ReadOnlyHiraganaRepository hiraganaRepository;
    TaskRepository taskRepository;
    public String checkAnswer(long userId, String receivedMessage) {
        TaskEntity task = taskRepository.findFirstByUserEntity_UserIdOrderByTaskIdDesc(userId)
                .orElseThrow(RuntimeException::new);
        String returnText = "Error";
        if (task.getQuestionNumber() <= task.getQuestionCount()) {
            String letterKey = task.getLetters().get(task.getQuestionNumber() - 1).getLetterKey();
            String letterValue;
            if (katakanaRepository.existsByHieroglyph(letterKey)) {
                letterValue = katakanaRepository.findByHieroglyph(letterKey).orElseThrow(RuntimeException::new)
                        .getHieroglyphPronouns();
            } else if (katakanaRepository.existsByHieroglyphPronouns(letterKey)) {
                letterValue = katakanaRepository.findByHieroglyphPronouns(letterKey).orElseThrow(RuntimeException::new)
                        .getHieroglyph();
            } else if (hiraganaRepository.existsByHieroglyph(letterKey)) {
                letterValue = hiraganaRepository.findByHieroglyph(letterKey).orElseThrow(RuntimeException::new)
                        .getHieroglyphPronouns();
            } else if (hiraganaRepository.existsByHieroglyphPronouns(letterKey)) {
                letterValue = hiraganaRepository.findByHieroglyphPronouns(letterKey).orElseThrow(RuntimeException::new)
                        .getHieroglyph();
            } else {
                return "Error";
            }
            boolean isCorrect;
            if (receivedMessage.equals(letterValue)) {
                returnText = "Correct";
                isCorrect = true;
            } else {
                returnText = "Wrong";
                isCorrect = false;
            }
            List<Boolean> answers = task.getIsAnswerCorrect();
            answers.add(isCorrect);
            int currentNumber = task.getQuestionNumber();
            task.setQuestionNumber(currentNumber + 1);
            taskRepository.save(task);
            if (task.getQuestionNumber() > task.getQuestionCount()) {
                returnText = returnText + "\n\nYour answers: " + task.getIsAnswerCorrect().toString();
                closeTask(userId);
            } else {
                letterValue = task.getLetters().get(task.getQuestionNumber() - 1).getLetterKey();
                returnText = returnText + "\n\nLetter - " + letterValue + "\nPronouns?";
            }
        }
        return returnText;
    }

    public void closeTask(long userId) {
        UserEntity user = userRepository.findByUserId(userId);
        user.setMode(UserMode.TEXT_MODE.name());
        userRepository.save(user);
    }

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
        task.setLetters(generateLetters(settings, task));
        taskRepository.save(task);
        return task;
    }

    private List<TaskLettersEntity> generateLetters(TaskSettingsEntity settings, TaskEntity task) {
        List<TaskLettersEntity> taskLettersEntities = new ArrayList<>();
        List<String> lettersList = generateLettersList(settings);
        int lettersListSize = lettersList.size();
        for (int i = 0; i < lettersListSize / 2; i++) {
            String letterKey = lettersList.remove(0);
            String letterValue = lettersList.remove(0);
            taskLettersEntities.add(TaskLettersEntity.builder()
                    .letterKey(letterKey)
                    .letterValue(letterValue)
                    .taskEntity(task)
                    .build());
        }
        return taskLettersEntities;
    }

    private List<String> generateLettersList(TaskSettingsEntity settings) {
        List<String> lettersList = new LinkedList<>();
        int count = settings.getQuestionCount();
        Random random = new Random();
        if (settings.getLetterGroup().equals("HIRAGANA")) {
            long repoLength = hiraganaRepository.count();
            if (settings.getUseLetters()) {
                for (int i = 0; i < count; i++) {
                    String letterKey = hiraganaRepository.findByHieroglyphId(random.nextLong(repoLength) + 1)
                            .orElseThrow(RuntimeException::new)
                            .getHieroglyph();
                    String letterValue = hiraganaRepository.findByHieroglyph(letterKey)
                            .orElseThrow(RuntimeException::new)
                            .getHieroglyphPronouns();
                    lettersList.add(letterKey);
                    lettersList.add(letterValue);
                }
            } else if (settings.getUsePronouns()) {
                for (int i = 0; i < count; i++) {
                    String letterValue = hiraganaRepository.findByHieroglyphId(random.nextLong(repoLength) + 1)
                            .orElseThrow(RuntimeException::new)
                            .getHieroglyph();
                    String letterKey = hiraganaRepository.findByHieroglyph(letterValue)
                            .orElseThrow(RuntimeException::new)
                            .getHieroglyphPronouns();
                    lettersList.add(letterKey);
                    lettersList.add(letterValue);
                }
            }
        } else if (settings.getLetterGroup().equals("KATAKANA")) {
            long repoLength = katakanaRepository.count();
            if (settings.getUseLetters()) {
                for (int i = 0; i < count; i++) {
                    String letterKey = katakanaRepository.findByHieroglyphId(random.nextLong(repoLength) + 1)
                            .orElseThrow(RuntimeException::new)
                            .getHieroglyph();
                    String letterValue = katakanaRepository.findByHieroglyph(letterKey)
                            .orElseThrow(RuntimeException::new)
                            .getHieroglyphPronouns();
                    lettersList.add(letterKey);
                    lettersList.add(letterValue);
                }
            } else if (settings.getUsePronouns()) {
                for (int i = 0; i < count; i++) {
                    String letterValue = katakanaRepository.findByHieroglyphId(random.nextLong(repoLength) + 1)
                            .orElseThrow(RuntimeException::new)
                            .getHieroglyph();
                    String letterKey = katakanaRepository.findByHieroglyph(letterValue)
                            .orElseThrow(RuntimeException::new)
                            .getHieroglyphPronouns();
                    lettersList.add(letterKey);
                    lettersList.add(letterValue);
                }
            }
        }
        return lettersList;
    }
}
