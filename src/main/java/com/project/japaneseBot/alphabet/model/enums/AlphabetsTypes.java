package com.project.japaneseBot.alphabet.model.enums;

import java.util.List;

public enum AlphabetsTypes {
    KATAKANA, HIRAGANA, ALL;

    public static List<AlphabetsTypes> getAlphabetsTypesList() {
        return List.of(KATAKANA, HIRAGANA, ALL);
    }
}
