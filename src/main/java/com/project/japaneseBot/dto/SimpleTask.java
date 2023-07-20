package com.project.japaneseBot.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record SimpleTask(
        int questionNumber,
        int questionCount,
        List<String> letter,
        List<String> pronouns,
        boolean[] isAnswerCorrect
) {}
