package com.project.japaneseBot.bot.service;

import com.project.japaneseBot.task.model.entity.TaskEntity;
import com.project.japaneseBot.task.model.entity.TaskLettersEntity;
import com.project.japaneseBot.task.repository.TaskRepository;
import com.project.japaneseBot.user.model.entity.UserEntity;
import com.project.japaneseBot.user.model.enums.UserMode;
import com.project.japaneseBot.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    UserRepository userRepository;
    TaskRepository taskRepository;

    public String getUserMode(long userId) {
        if (userRepository.existsByUserId(userId)) {
            return userRepository.findByUserId(userId).getMode();
        } else {
            return UserMode.GUEST_MODE.name();
        }
    }

    private void changeMode(long userId, UserMode userMode) {
        UserEntity user = userRepository.findByUserId(userId);
        user.setMode(userMode.name());
        userRepository.save(user);
    }

    public void startTextMode(long userId) {
        changeMode(userId, UserMode.TEXT_MODE);
    }

    public void startSettingsMode(long userId) {
        changeMode(userId, UserMode.SETT_MODE);
    }

    public void startTaskMode(long userId) {
        changeMode(userId, UserMode.TASK_MODE);
    }

    public void startEditSettingsMode(long userId) {
        changeMode(userId, UserMode.EDIT_SETT_MODE);
    }

    public String getUserProfile(long userId) {
        if (userRepository.existsByUserId(userId)) {
            UserEntity user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
            return "Data of creation: " + user.getRegistrationDate().toString() +
                    "\nTasks completed: " + taskRepository.countByUserEntity_UserId(userId) +
                    "\nPercent of correct answers: " + countCorrectAnswersPercent(user) +
                    "\nLast mistake: " + getLastMistake(user);
        } else {
            return "You don't have an account. Create one using the command /register";
        }
    }

    private String getLastMistake(UserEntity userEntity) {
        List<TaskEntity> tasksList = userEntity.getTasks();
        while (true) {
            if (!tasksList.isEmpty()) {
                TaskEntity task = tasksList.remove(tasksList.size() - 1);
                List<Boolean> taskAnswersList = task.getIsAnswerCorrect();
                for (int j = taskAnswersList.size() - 1; j > 0; j-- ) {
                    if (!taskAnswersList.get(j)) {
                        TaskLettersEntity failedLetter = task.getLetters().get(j);
                        return failedLetter.getLetterKey() + " - " + failedLetter.getLetterValue();
                    }
                }
            } else {
                break;
            }

        }
        return "You have no mistakes";
    }

    private String countCorrectAnswersPercent(UserEntity user) {
        List<TaskEntity> taskList = user.getTasks();
        if (taskList.isEmpty()) {
            return "You have no completed tasks, use /task to start a task";
        }
        int correctAnswers = 0;
        int wrongAnswers = 0;
        for (TaskEntity task : taskList) {
            List<Boolean> answerList = task.getIsAnswerCorrect();
            for (Boolean answer : answerList) {
                if (answer) {
                    correctAnswers++;
                } else {
                    wrongAnswers++;
                }
            }
        }
        float correctAnswersPercent = (float) correctAnswers / (correctAnswers + wrongAnswers) * 100;
        return String.format("%.1f", correctAnswersPercent) + "%";
    }

    public String createUser(long userId, String userName) {
        if (userRepository.existsByUserId(userId)) {
            return "You are already registered, enjoy using the bot, " + userName;
        } else {
            userRepository.save(UserEntity.builder()
                    .userId(userId)
                    .registrationDate(LocalDate.now())
                    .mode(UserMode.TEXT_MODE.name())
                    .build());
            return "Account created";
        }

    }
}
