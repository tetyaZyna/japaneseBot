package com.project.japaneseBot.bot.buttons;

import com.project.japaneseBot.alphabet.repository.ReadOnlyHiraganaRepository;
import com.project.japaneseBot.alphabet.repository.ReadOnlyKatakanaRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class Keyboards {
    private final ReadOnlyKatakanaRepository katakanaRepository;
    private final ReadOnlyHiraganaRepository hiraganaRepository;

    private ReplyKeyboardMarkup katakanaReplyKeyboardMarkup;
    private ReplyKeyboardMarkup hiraganaReplyKeyboardMarkup;

    public Keyboards (ReadOnlyKatakanaRepository katakanaRepository, ReadOnlyHiraganaRepository hiraganaRepository) {
        this.katakanaRepository = katakanaRepository;
        this.hiraganaRepository = hiraganaRepository;
        initializeKatakanaReplyKeyboard();
        initializeHiraganaReplyKeyboard();
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
                katakana = katakanaRepository.findByHieroglyphId(i)
                        .orElseThrow(RuntimeException::new)
                        .getHieroglyph();
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
                hiragana = hiraganaRepository.findByHieroglyphId(i)
                        .orElseThrow(RuntimeException::new)
                        .getHieroglyph();
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
