package com.project.japaneseBot.bot.service;

import com.project.japaneseBot.task.repository.TaskRepository;
import com.project.japaneseBot.user.model.entity.UserEntity;
import com.project.japaneseBot.user.model.enums.UserMode;
import com.project.japaneseBot.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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
                    "\nTasks completed: " + taskRepository.countByUserEntity_UserId(userId);
        } else {
            return "You don't have an account. Create one using the command /register";
        }
    }

    public String createUser(long userId, String userName) {
        if (userRepository.existsByUserId(userId)) {
            return "You are already registered, enjoy using the bot, " + userName;
        } else {
            userRepository.save(UserEntity.builder()
                    .userId(userId)
                    .registrationDate(LocalDate.now())
                    .mode("TEXT_MODE")
                    .build());
            return "Account created";
        }

    }
}
