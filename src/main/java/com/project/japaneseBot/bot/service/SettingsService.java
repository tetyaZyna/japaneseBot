package com.project.japaneseBot.bot.service;

import com.project.japaneseBot.alphabet.repository.AlphabetsRepository;
import com.project.japaneseBot.task.model.entity.TaskSettingsEntity;
import com.project.japaneseBot.task.repository.TaskRepository;
import com.project.japaneseBot.task.repository.TaskSettingsRepository;
import com.project.japaneseBot.user.model.entity.UserEntity;
import com.project.japaneseBot.user.model.enums.UserMode;
import com.project.japaneseBot.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SettingsService {
    UserRepository userRepository;
    TaskRepository taskRepository;
    AlphabetsRepository alphabetsRepository;
    TaskSettingsRepository settingsRepository;
    UserService userService;

    public TaskSettingsEntity findSettings(String settingsName) {
        return settingsRepository.findBySettingsName(settingsName);
    }

    public void save(TaskSettingsEntity settings) {
        settingsRepository.save(settings);
    }
}
