package com.project.japaneseBot.alphabet.model.enums;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public enum LettersGroup {
    ALL("All letters"),
    VOWEL("a - o and ya, yu, yo"),
    K_CONSONANT("ka - ko"),
    S_CONSONANT("sa - so"),
    T_CONSONANT("ta - to"),
    N_CONSONANT("na - no"),
    H_CONSONANT("ha - ho"),
    M_CONSONANT("ma - mo"),
    R_CONSONANT("ra - ro"),
    W_CONSONANT("wa - wo"),
    NASAL("n"),
    G_CONSONANT("ga - go"),
    Z_CONSONANT("za - zo"),
    D_CONSONANT("da - do"),
    B_CONSONANT("ba - bo"),
    P_CONSONANT("pa - po");

    private final String description;

    LettersGroup(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
    public static List<LettersGroup> getAllLettersGroups() {
        return Arrays.asList(LettersGroup.values());
    }
}
