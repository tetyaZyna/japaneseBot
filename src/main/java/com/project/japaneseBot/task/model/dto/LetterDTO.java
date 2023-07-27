package com.project.japaneseBot.task.model.dto;

import lombok.Builder;

@Builder
public record LetterDTO(
        String letterKey,
        String letterAlphabet,
        String letterValue
) {}
