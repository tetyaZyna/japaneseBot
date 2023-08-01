package com.project.japaneseBot.bot.buttons;

import com.project.japaneseBot.task.model.entity.TaskSettingsEntity;
import com.project.japaneseBot.task.repository.TaskSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class Buttons {
    @Autowired
    TaskSettingsRepository settingsRepository;

    private final InlineKeyboardButton HELP_BUTTON = new InlineKeyboardButton("Help");
    private final InlineKeyboardButton KATAKANA_BUTTON = new InlineKeyboardButton("Katakana");
    private final InlineKeyboardButton HIRAGANA_BUTTON = new InlineKeyboardButton("Hiragana");

    public InlineKeyboardMarkup inlineMarkup() {
        HELP_BUTTON.setCallbackData("/help");

        List<InlineKeyboardButton> rowInline = List.of(HELP_BUTTON);
        List<List<InlineKeyboardButton>> rowsInLine = List.of(rowInline);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);

        return markupInline;
    }

    @Deprecated
    public InlineKeyboardMarkup alphabetsChoose() {
        KATAKANA_BUTTON.setCallbackData("/katakana");
        HIRAGANA_BUTTON.setCallbackData("/hiragana");

        List<InlineKeyboardButton> rowInline = List.of(KATAKANA_BUTTON, HIRAGANA_BUTTON);
        List<List<InlineKeyboardButton>> rowsInLine = List.of(rowInline);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);

        return markupInline;
    }

    public InlineKeyboardMarkup inlineSettingsKeyboard() {
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        String settingsName;
        List<TaskSettingsEntity> settingsList = settingsRepository.findAll();
        int rowCount = 0;
        for (TaskSettingsEntity settings : settingsList) {
            if (rowCount == 2) {
                rowsInLine.add(rowInline);
                rowInline = new ArrayList<>();
                rowCount = 0;
            }
            settingsName = settings.getSettingsName();
            InlineKeyboardButton settButton = new InlineKeyboardButton(settingsName);
            settButton.setCallbackData(settingsName);
            rowInline.add(settButton);
            rowCount++;
        }
        rowsInLine.add(rowInline);
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);

        return markupInline;
    }
}
