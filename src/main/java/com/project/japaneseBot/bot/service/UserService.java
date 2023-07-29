package com.project.japaneseBot.bot.service;

import com.project.japaneseBot.user.model.entity.UserEntity;
import com.project.japaneseBot.user.model.enums.UserMode;
import com.project.japaneseBot.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    UserRepository userRepository;

    public String getUserMode(long userId) {
        if (userRepository.existsByUserId(userId)) {
            return userRepository.findByUserId(userId).getMode();
        } else {
            return "GUEST_MODE";
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
}
