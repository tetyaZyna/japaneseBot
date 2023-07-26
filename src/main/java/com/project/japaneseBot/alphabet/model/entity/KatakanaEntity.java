package com.project.japaneseBot.alphabet.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import static jakarta.persistence.GenerationType.IDENTITY;

@Deprecated
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Validated
@Table(name = "katakana")
public class KatakanaEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long hieroglyphId;

    private String hieroglyph;

    private String hieroglyphPronouns;
}

