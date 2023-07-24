package com.project.japaneseBot.bot.service;

import com.project.japaneseBot.alphabet.repository.ReadOnlyHiraganaRepository;
import com.project.japaneseBot.alphabet.repository.ReadOnlyKatakanaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
@AllArgsConstructor
public class HandlerService {
    ReadOnlyKatakanaRepository katakanaRepository;
    ReadOnlyHiraganaRepository hiraganaRepository;

    public String handleMessage(String message) {
        List<String> letters = new LinkedList<>();
        List<String> lettersPronouns = new LinkedList<>();
        if (message.length() == 1) {
            List<String> letterAndPronouns = handleLetter(message);
            if (!letterAndPronouns.isEmpty()) {
                letters.add(letterAndPronouns.get(0));
                lettersPronouns.add(letterAndPronouns.get(1));
            }
        } else {
            for (int i = 0; i < message.length(); i++) {
                char letter = message.charAt(i);
                List<String> letterAndPronouns = handleLetter(String.valueOf(letter));
                if (!letterAndPronouns.isEmpty()) {
                    letters.add(letterAndPronouns.get(0));
                    lettersPronouns.add(letterAndPronouns.get(1));
                }
            }
        }
        if (letters.isEmpty() || lettersPronouns.isEmpty()) {
            return "";
        } else {
            return String.join("", letters) + " - " + String.join("", lettersPronouns);
        }
    }

     protected List<String> handleLetter(String letter) {
        if (katakanaRepository.existsByHieroglyph(letter)) {
            return handleKatakanaValue(letter);
        } else if (hiraganaRepository.existsByHieroglyph(letter)) {
            return handleHiraganaValue(letter);
        }
        return new ArrayList<>();
    }

    private List<String> handleKatakanaValue(String katakanaValue) {
        String hieroglyphPronouns = katakanaRepository.findByHieroglyph(katakanaValue)
                .orElseThrow(RuntimeException::new).getHieroglyphPronouns();
        return List.of(katakanaValue, hieroglyphPronouns);
    }

    private List<String> handleHiraganaValue(String hiraganaValue) {
        String hieroglyphPronouns = hiraganaRepository.findByHieroglyph(hiraganaValue)
                .orElseThrow(RuntimeException::new).getHieroglyphPronouns();
        return List.of(hiraganaValue, hieroglyphPronouns);
    }
}
