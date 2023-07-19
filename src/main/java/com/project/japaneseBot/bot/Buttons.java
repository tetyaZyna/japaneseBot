package com.project.japaneseBot.bot;

import com.project.japaneseBot.alphabet.repository.ReadOnlyHiraganaRepository;
import com.project.japaneseBot.alphabet.repository.ReadOnlyKatakanaRepository;
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
    private final ReadOnlyKatakanaRepository katakanaRepository;
    private final ReadOnlyHiraganaRepository hiraganaRepository;

    private ReplyKeyboardMarkup katakanaReplyKeyboardMarkup;
    private ReplyKeyboardMarkup hiraganaReplyKeyboardMarkup;

    private final InlineKeyboardButton START_BUTTON = new InlineKeyboardButton("Start");
    private final InlineKeyboardButton HELP_BUTTON = new InlineKeyboardButton("Help");
    private final InlineKeyboardButton KATAKANA_BUTTON = new InlineKeyboardButton("Katakana");
    private final InlineKeyboardButton HIRAGANA_BUTTON = new InlineKeyboardButton("Hiragana");

    public Buttons (ReadOnlyKatakanaRepository katakanaRepository, ReadOnlyHiraganaRepository hiraganaRepository) {
        this.katakanaRepository = katakanaRepository;
        this.hiraganaRepository = hiraganaRepository;
        initializeKatakanaReplyKeyboard();
        initializeHiraganaReplyKeyboard();
    }

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

    public ReplyKeyboardMarkup getKatakanaReplyKeyboard () {
        return katakanaReplyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getHiraganaReplyKeyboard () {
        return hiraganaReplyKeyboardMarkup;
    }

    private void initializeKatakanaReplyKeyboard() {
        katakanaReplyKeyboardMarkup = new ReplyKeyboardMarkup();
        katakanaReplyKeyboardMarkup.setResizeKeyboard(true);
        katakanaReplyKeyboardMarkup.setSelective(true);
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardRows = new KeyboardRow();
        String katakana;
        long repositoryLength = katakanaRepository.count();
        for (long i = 1; i <= repositoryLength; i++) {
            if (i % 10 == 1 && i > 1) {
                keyboardRowList.add(keyboardRows);
                keyboardRows = new KeyboardRow();
            }
            if (katakanaRepository.existsById(i)) {
                katakana = katakanaRepository.findByHieroglyphId(i).getHieroglyph();
                KeyboardButton button = new KeyboardButton(katakana);
                keyboardRows.add(button);
            }
        }
        KeyboardButton switchButton = new KeyboardButton("/hiragana");
        keyboardRows.add(switchButton);
        keyboardRowList.add(keyboardRows);
        katakanaReplyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    private void initializeHiraganaReplyKeyboard() {
        hiraganaReplyKeyboardMarkup = new ReplyKeyboardMarkup();
        hiraganaReplyKeyboardMarkup.setResizeKeyboard(true);
        hiraganaReplyKeyboardMarkup.setSelective(true);
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardRows = new KeyboardRow();
        String hiragana;
        long repositoryLength = hiraganaRepository.count();
        for (long i = 1; i <= repositoryLength; i++) {
            if (i % 10 == 1 && i > 1) {
                keyboardRowList.add(keyboardRows);
                keyboardRows = new KeyboardRow();
            }
            if (hiraganaRepository.existsById(i)) {
                hiragana = hiraganaRepository.findByHieroglyphId(i).getHieroglyph();
                KeyboardButton button = new KeyboardButton(hiragana);
                keyboardRows.add(button);
            }
        }
        KeyboardButton switchButton = new KeyboardButton("/katakana");
        keyboardRows.add(switchButton);
        keyboardRowList.add(keyboardRows);
        hiraganaReplyKeyboardMarkup.setKeyboard(keyboardRowList);
    }
}
