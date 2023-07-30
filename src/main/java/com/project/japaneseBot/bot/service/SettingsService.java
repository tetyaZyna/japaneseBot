package com.project.japaneseBot.bot.service;

import com.project.japaneseBot.alphabet.repository.AlphabetsRepository;
import com.project.japaneseBot.task.model.entity.TaskSettingsEntity;
import com.project.japaneseBot.task.repository.TaskRepository;
import com.project.japaneseBot.task.repository.TaskSettingsRepository;
import com.project.japaneseBot.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void createAndSaveSettings(String receivedMessage) {
        List<String> receivedSettings = List.of(receivedMessage.split(" "));
        settingsRepository.save(TaskSettingsEntity.builder()
                .settingsName(receivedSettings.get(1))
                .questionCount(Integer.parseInt(receivedSettings.get(2)))
                .alphabet(receivedSettings.get(3))
                .letterGroup("ALL")
                .usePronouns(Boolean.parseBoolean(receivedSettings.get(4)))
                .useLetters(Boolean.parseBoolean(receivedSettings.get(5)))
                .build());
    }
}
