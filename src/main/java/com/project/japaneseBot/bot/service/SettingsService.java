package com.project.japaneseBot.bot.service;

import com.project.japaneseBot.alphabet.model.enums.AlphabetsTypes;
import com.project.japaneseBot.alphabet.repository.AlphabetsRepository;
import com.project.japaneseBot.config.TaskConfig;
import com.project.japaneseBot.task.model.entity.TaskSettingsEntity;
import com.project.japaneseBot.task.repository.TaskRepository;
import com.project.japaneseBot.task.repository.TaskSettingsRepository;
import com.project.japaneseBot.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class SettingsService {
    UserRepository userRepository;
    TaskRepository taskRepository;
    AlphabetsRepository alphabetsRepository;
    TaskSettingsRepository settingsRepository;
    UserService userService;
    TaskConfig taskConfig;

    public TaskSettingsEntity findSettings(String settingsName) {
        return settingsRepository.findBySettingsName(settingsName);
    }

    private boolean isValidQuestionCount(int questionCount) {
        return questionCount >= taskConfig.questionsMinValue()
                && questionCount <= taskConfig.questionsMaxValue();
    }

    private boolean isValidAlphabet(String alphabet) {
        List<AlphabetsTypes> alphabetsTypesList = AlphabetsTypes.getAlphabetsTypesList();
        for (AlphabetsTypes types : alphabetsTypesList) {
            if (types.name().equalsIgnoreCase(alphabet)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    private boolean isValidSettings(List<String> receivedSettings) {
        if (receivedSettings.size() < 6) {
            return false;
        }
        int questionCount;
        String alphabet;
        try {
            questionCount = Integer.parseInt(receivedSettings.get(2));
            alphabet = receivedSettings.get(3).toUpperCase();
            boolean usePronouns = Boolean.parseBoolean(receivedSettings.get(4));
            boolean useLetters = Boolean.parseBoolean(receivedSettings.get(5));
        } catch (NumberFormatException e) {
            log.error(e.getMessage());
            return false;
        }
        return isValidQuestionCount(questionCount) && isValidAlphabet(alphabet);
    }

    public String createAndSaveSettings(String receivedMessage) {
        List<String> receivedSettings = List.of(receivedMessage.split(" "));
        if (!isValidSettings(receivedSettings)) {
            return null;
        }
        String settingsName = receivedSettings.get(1);
        int questionCount = Integer.parseInt(receivedSettings.get(2));
        String alphabet = receivedSettings.get(3).toUpperCase();
        boolean usePronouns = Boolean.parseBoolean(receivedSettings.get(4));
        boolean useLetters = Boolean.parseBoolean(receivedSettings.get(5));
        settingsRepository.save(TaskSettingsEntity.builder()
                .settingsName(settingsName)
                .questionCount(questionCount)
                .alphabet(alphabet)
                .letterGroup("ALL")
                .usePronouns(usePronouns)
                .useLetters(useLetters)
                .build());
        return "Settings created";
    }

    public TaskSettingsEntity getSettingsByName(String receivedMessage) {
        List<String> receivedMessageParts = List.of(receivedMessage.split(" "));
        TaskSettingsEntity settings = findSettings(receivedMessageParts.get(0));
        if (receivedMessageParts.size() > 1) {
            try {
                int questionCount = Integer.parseInt(receivedMessageParts.get(1));
                if (isValidQuestionCount(questionCount)) {
                   settings.setQuestionCount(questionCount);
                }
            } catch (NumberFormatException e) {
                log.error(e.getMessage());
            }
        }
        return settings;
    }
}
