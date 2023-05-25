package com.project.japaneseBot.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Slf4j
@ConfigurationProperties(prefix = "bot")
public record BotConfig(
        String name,
        String token
) {
    @PostConstruct
    void logLoaded() {
        log.info("props = {}", this);
    }
}
