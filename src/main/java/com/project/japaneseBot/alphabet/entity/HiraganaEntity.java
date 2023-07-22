package com.project.japaneseBot.alphabet.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "hiragana")
public class HiraganaEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long hieroglyphId;

    private String hieroglyph;

    private String hieroglyphPronouns;
}
