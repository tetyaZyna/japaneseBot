package com.project.japaneseBot.bot.service;

import com.project.japaneseBot.alphabet.repository.ReadOnlyHiraganaRepository;
import com.project.japaneseBot.alphabet.repository.ReadOnlyKatakanaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@AllArgsConstructor
public class HandlerService {
    ReadOnlyKatakanaRepository katakanaRepository;
    ReadOnlyHiraganaRepository hiraganaRepository;

    public String handleMessage(String message) {
        if (message.length() == 1) {
            if (katakanaRepository.existsByHieroglyph(message)) {
                String[] value = handleKatakanaValue(message);
                return value[0] + " - " + value[1];
            } else if (hiraganaRepository.existsByHieroglyph(message)) {
                String[] value = handleHiraganaValue(message);
                return value[0] + " - " + value[1];
            }
        }
        List<String> letters = new LinkedList<>();
        List<String> lettersPronouns = new LinkedList<>();
        for (int i = 0; i < message.length(); i++) {
            char letter = message.charAt(i);
            String[] letterAndPronouns = handleLetter(String.valueOf(letter));
            if (letterAndPronouns != null) {
                letters.add(letterAndPronouns[0]);
                lettersPronouns.add(letterAndPronouns[1]);
            }
        }
        return String.join("", letters) + " - " + String.join("", lettersPronouns);
    }

    private String[] handleLetter(String letter) {
        if (katakanaRepository.existsByHieroglyph(letter)) {
            return handleKatakanaValue(letter);
        } else if (hiraganaRepository.existsByHieroglyph(letter)) {
            return handleHiraganaValue(letter);
        } else if (letter.equals(" ")) {
            return new String[]{" ", " "};
        }
        return null;
    }

    private String[] handleKatakanaValue(String katakanaValue) {
        String hieroglyphPronouns = katakanaRepository.findByHieroglyph(katakanaValue)
                .orElseThrow(RuntimeException::new).getHieroglyphPronouns();
        return new String[]{katakanaValue, hieroglyphPronouns};
    }

    private String[] handleHiraganaValue(String hiraganaValue) {
        String hieroglyphPronouns = hiraganaRepository.findByHieroglyph(hiraganaValue)
                .orElseThrow(RuntimeException::new).getHieroglyphPronouns();
        return new String[]{hiraganaValue, hieroglyphPronouns};
    }
}
