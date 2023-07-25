package com.project.japaneseBot.bot.buttons;

import com.project.japaneseBot.task.repository.TaskSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class Buttons {
    @Autowired
    TaskSettingsRepository settingsRepository;

    private final InlineKeyboardButton START_BUTTON = new InlineKeyboardButton("Start");
    private final InlineKeyboardButton HELP_BUTTON = new InlineKeyboardButton("Help");
    private final InlineKeyboardButton KATAKANA_BUTTON = new InlineKeyboardButton("Katakana");
    private final InlineKeyboardButton HIRAGANA_BUTTON = new InlineKeyboardButton("Hiragana");

    public InlineKeyboardMarkup inlineMarkup() {
        START_BUTTON.setCallbackData("/start");
        HELP_BUTTON.setCallbackData("/help");

        List<InlineKeyboardButton> rowInline = List.of(START_BUTTON, HELP_BUTTON);
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
        long repositoryLength = settingsRepository.count();
        for (long i = 1; i <= repositoryLength; i++) {
            if (i % 2 == 1 && i > 1) {
                rowsInLine.add(rowInline);
                rowInline = new ArrayList<>();
            }
            if (settingsRepository.existsById(i)) {
                settingsName = settingsRepository.findById(i)
                        .orElseThrow(RuntimeException::new)
                        .getSettingsName();
                InlineKeyboardButton settButton = new InlineKeyboardButton(settingsName);
                settButton.setCallbackData(settingsName);
                rowInline.add(settButton);
            }
        }
        rowsInLine.add(rowInline);
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);

        return markupInline;
    }
}
