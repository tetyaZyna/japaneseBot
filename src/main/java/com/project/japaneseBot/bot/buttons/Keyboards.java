package com.project.japaneseBot.bot.buttons;

import com.project.japaneseBot.alphabet.model.entity.AlphabetsEntity;
import com.project.japaneseBot.alphabet.model.enums.AlphabetRepresentation;
import com.project.japaneseBot.alphabet.model.enums.AlphabetsTypes;
import com.project.japaneseBot.alphabet.repository.AlphabetsRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class Keyboards {
    private final AlphabetsRepository alphabetsRepository;
    private ReplyKeyboardMarkup katakanaReplyKeyboardMarkup;
    private ReplyKeyboardMarkup hiraganaReplyKeyboardMarkup;
    private ReplyKeyboardMarkup pronounsReplyKeyboardMarkup;

    @Deprecated
    public Keyboards(AlphabetsRepository alphabetsRepository) {
        this.alphabetsRepository = alphabetsRepository;
        initializeAlphabetsReplyKeyboards();
    }

    public ReplyKeyboardMarkup getPronounsReplyKeyboardMarkup() {
        return pronounsReplyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getKatakanaReplyKeyboard() {
        return katakanaReplyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getHiraganaReplyKeyboard() {
        return hiraganaReplyKeyboardMarkup;
    }

    private void initializeAlphabetsReplyKeyboards() {
        List<KeyboardRow> katakanaKeyboardRowList = createKeyboardRowsForAlphabet(AlphabetsTypes.KATAKANA,
                AlphabetRepresentation.LETTER);
        List<KeyboardRow> hiraganaKeyboardRowList = createKeyboardRowsForAlphabet(AlphabetsTypes.HIRAGANA,
                AlphabetRepresentation.LETTER);
        List<KeyboardRow> pronounsKeyboardRowList = createKeyboardRowsForAlphabet(AlphabetsTypes.KATAKANA,
                AlphabetRepresentation.PRONOUNS);
        addNavigationButtons(katakanaKeyboardRowList, "/pronouns", "/hiragana");
        addNavigationButtons(hiraganaKeyboardRowList, "/pronouns", "/katakana");
        addNavigationButtons(pronounsKeyboardRowList, "/katakana", "/hiragana");
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
        pronounsReplyKeyboardMarkup = ReplyKeyboardMarkup.builder()
                .keyboard(pronounsKeyboardRowList)
                .resizeKeyboard(true)
                .selective(true)
                .build();
    }

    private List<KeyboardRow> createKeyboardRowsForAlphabet(AlphabetsTypes alphabetType,
                                                            AlphabetRepresentation alphabetRepresentation) {
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
            String letter;
            if (alphabetRepresentation.equals(AlphabetRepresentation.LETTER)){
                letter = alphabetsEntity.getLetter();
            } else if (alphabetRepresentation.equals(AlphabetRepresentation.PRONOUNS)) {
                letter = alphabetsEntity.getLetterPronouns();
            } else {
                throw new RuntimeException("Unknown value of enum " + alphabetRepresentation.name());
            }
            KeyboardButton button = new KeyboardButton(letter);
            keyboardRow.add(button);
            buttonsInRow++;
        }
        keyboardRowList.add(keyboardRow);
        return keyboardRowList;
    }

    private void addNavigationButtons(List<KeyboardRow> keyboardRowList, String leftButtonText, String rightButtonText) {
        int lastElementIndex = keyboardRowList.size() - 1;
        KeyboardRow navigationRow = keyboardRowList.remove(lastElementIndex);
        KeyboardButton leftNavigationButton = new KeyboardButton(leftButtonText);
        KeyboardButton rightNavigationButton = new KeyboardButton(rightButtonText);
        navigationRow.add(0, leftNavigationButton);
        navigationRow.add(rightNavigationButton);
        keyboardRowList.add(navigationRow);
    }
}