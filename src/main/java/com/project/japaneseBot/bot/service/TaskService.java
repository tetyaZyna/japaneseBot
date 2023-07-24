package com.project.japaneseBot.bot.service;

import com.project.japaneseBot.alphabet.repository.ReadOnlyHiraganaRepository;
import com.project.japaneseBot.alphabet.repository.ReadOnlyKatakanaRepository;
import com.project.japaneseBot.bot.model.UserMode;
import com.project.japaneseBot.task.entity.TaskEntity;
import com.project.japaneseBot.task.entity.TaskLettersEntity;
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
            String letter = task.getLetters().get(task.getQuestionNumber() - 1).getLetterKey();
            String pronouns = katakanaRepository.findByHieroglyph(letter).orElseThrow(RuntimeException::new)
                    .getHieroglyphPronouns();
            boolean isCorrect;
            if (receivedMessage.equals(pronouns)) {
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
                letter = task.getLetters().get(task.getQuestionNumber() - 1).getLetterKey();
                returnText = returnText + "\n\nLetter - " + letter + "\nPronouns?";
            }
        }
        return returnText;
    }

    public void closeTask(long userId) {
        UserEntity user = userRepository.findByUserId(userId);
        user.setMode(UserMode.TEXT_MODE.name());
        userRepository.save(user);
    }

    public String initialiseTask(long userId) {
        UserEntity user = userRepository.findByUserId(userId);
        int questionCount = 10;
        TaskEntity task = createAndSaveTask(user, questionCount);
        user.setMode(UserMode.TASK_MODE.name());
        userRepository.save(user);
        var letter = task.getLetters().get(task.getQuestionNumber() - 1).getLetterKey();
        return "Letter - " + letter + "\nPronouns?";
    }

    @Transactional
    public TaskEntity createAndSaveTask(UserEntity user, int questionCount) {
        TaskEntity task = TaskEntity.builder()
                .questionNumber(1)
                .questionCount(questionCount)
                .isAnswerCorrect(new LinkedList<>())
                .userEntity(user)
                .build();
        task.setLetters(generateLetters(questionCount, task));
        taskRepository.save(task);
        return task;
    }

    private List<TaskLettersEntity> generateLetters(int count, TaskEntity task) {
        List<TaskLettersEntity> taskLettersEntities = new ArrayList<>();
        long repositoryLength = katakanaRepository.count();
        List<Long> idList = new ArrayList<>();
        for (long j = 0; j < repositoryLength; j++) {
            idList.add(j + 1);
        }
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            String letterKey = katakanaRepository.findByHieroglyphId(idList
                            .remove(random.nextInt(idList.size())))
                    .orElseThrow(RuntimeException::new)
                    .getHieroglyph();
            String letterValue = katakanaRepository.findByHieroglyph(letterKey)
                    .orElseThrow(RuntimeException::new)
                    .getHieroglyphPronouns();
            taskLettersEntities.add(TaskLettersEntity.builder()
                    .letterKey(letterKey)
                    .letterValue(letterValue)
                    .taskEntity(task)
                    .build());
        }
        return taskLettersEntities;
    }
}
