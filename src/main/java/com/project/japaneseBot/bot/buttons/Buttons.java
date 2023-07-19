package com.project.japaneseBot.bot.buttons;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class Buttons {
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
}
