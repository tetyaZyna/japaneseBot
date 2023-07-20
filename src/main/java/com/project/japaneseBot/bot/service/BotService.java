package com.project.japaneseBot.bot.service;

import com.project.japaneseBot.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BotService {
    UserRepository userRepository;

    public String getUserMode(long userId) {
        if (userRepository.existsByUserId(userId)) {
            return userRepository.findByUserId(userId).getMode();
        } else {
            return "GUEST_MODE";
        }
    }
}
