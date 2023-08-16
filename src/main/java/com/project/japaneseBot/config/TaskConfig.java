package com.project.japaneseBot.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Slf4j
@ConfigurationProperties(prefix = "task")
public record TaskConfig(
        int questionsMinValue,
        int questionsMaxValue
) {
    @PostConstruct
    void logLoaded() {
        log.info("taskProps = {}", this);
    }
}
