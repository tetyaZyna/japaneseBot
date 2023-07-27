package com.project.japaneseBot.bot.service;

import com.project.japaneseBot.alphabet.repository.AlphabetsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
@AllArgsConstructor
public class HandlerService {
    AlphabetsRepository alphabetsRepository;

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

    protected List<String> findLetterPronunciation(String letter) {
        String letterPronouns = alphabetsRepository.findByLetter(letter).orElseThrow(RuntimeException::new)
                .getLetterPronouns();
        return List.of(letter, letterPronouns);
    }

     protected List<String> handleLetter(String letter) {
        if (alphabetsRepository.existsByLetter(letter)) {
            return findLetterPronunciation(letter);
        }
        return Collections.emptyList();
    }
}
