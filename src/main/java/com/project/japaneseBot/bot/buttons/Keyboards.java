package com.project.japaneseBot.bot.buttons;

import com.project.japaneseBot.alphabet.model.entity.AlphabetsEntity;
import com.project.japaneseBot.alphabet.model.enums.AlphabetsTypes;
import com.project.japaneseBot.alphabet.repository.AlphabetsRepository;
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
    @Deprecated
    private final ReadOnlyKatakanaRepository katakanaRepository;
    @Deprecated
    private final ReadOnlyHiraganaRepository hiraganaRepository;
    private final AlphabetsRepository alphabetsRepository;
    private ReplyKeyboardMarkup katakanaReplyKeyboardMarkup;
    private ReplyKeyboardMarkup hiraganaReplyKeyboardMarkup;

    @Deprecated
    public Keyboards (ReadOnlyKatakanaRepository katakanaRepository, ReadOnlyHiraganaRepository hiraganaRepository, AlphabetsRepository alphabetsRepository) {
        this.katakanaRepository = katakanaRepository;
        this.hiraganaRepository = hiraganaRepository;
        this.alphabetsRepository = alphabetsRepository;
        initializeAlphabetsReplyKeyboards();
    }

    public ReplyKeyboardMarkup getKatakanaReplyKeyboard () {
        return katakanaReplyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getHiraganaReplyKeyboard () {
        return hiraganaReplyKeyboardMarkup;
    }

    @Deprecated
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

    private void initializeAlphabetsReplyKeyboards() {
        List<KeyboardRow> katakanaKeyboardRowList = createKeyboardRowsForAlphabet(AlphabetsTypes.KATAKANA);
        List<KeyboardRow> hiraganaKeyboardRowList = createKeyboardRowsForAlphabet(AlphabetsTypes.HIRAGANA);
        addNavigationButtons(katakanaKeyboardRowList, "/hiragana");
        addNavigationButtons(hiraganaKeyboardRowList, "/katakana");
        katakanaReplyKeyboardMarkup = ReplyKeyboardMarkup.builder()
                .keyboard(katakanaKeyboardRowList)
                .resizeKeyboard(true)
                .selective(true)
                .build();
        hiraganaReplyKeyboardMarkup = ReplyKeyboardMarkup.builder()
                .keyboard(hiraganaKeyboardRowList)
                .resizeKeyboard(true)
                .selective(true)
                .build();
    }

    private List<KeyboardRow> createKeyboardRowsForAlphabet(AlphabetsTypes alphabetType) {
        int MAX_BUTTONS_PER_ROW = 11;
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        int buttonsInRow = 0;
        List<AlphabetsEntity> alphabetsList = alphabetsRepository.findByAlphabet(alphabetType.name());
        for (AlphabetsEntity alphabetsEntity : alphabetsList) {
            if (buttonsInRow == MAX_BUTTONS_PER_ROW) {
                keyboardRowList.add(keyboardRow);
                keyboardRow = new KeyboardRow();
                buttonsInRow = 0;
            }
            String letter = alphabetsEntity.getLetter();
            KeyboardButton button = new KeyboardButton(letter);
            keyboardRow.add(button);
            buttonsInRow++;
        }
        keyboardRowList.add(keyboardRow);
        return keyboardRowList;
    }

    private void addNavigationButtons(List<KeyboardRow> keyboardRowList, String buttonText) {
        int lastElementIndex = keyboardRowList.size() - 1;
        KeyboardRow navigationRow = keyboardRowList.remove(lastElementIndex);
        KeyboardButton navigationButton = new KeyboardButton(buttonText);
        navigationRow.add(navigationButton);
        keyboardRowList.add(navigationRow);
    }

    @Deprecated
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
