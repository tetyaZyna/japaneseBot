package com.project.japaneseBot.task.model.dto;

import lombok.Builder;

@Builder
public record LetterDTO(
        String letterKey,
        String keyType,
        String letterAlphabet,
        String letterValue
) {}
