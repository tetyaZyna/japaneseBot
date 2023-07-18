package com.project.japaneseBot.alphabet.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import static jakarta.persistence.GenerationType.IDENTITY;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Validated
public class KatakanaEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long hieroglyphId;
    private String hieroglyph;
    private String hieroglyphPronouns;
}

