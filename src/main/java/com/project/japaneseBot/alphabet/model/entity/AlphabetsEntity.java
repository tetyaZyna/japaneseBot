package com.project.japaneseBot.alphabet.model.entity;

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
@Table(name = "alphabets")
public class AlphabetsEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long letterId;

    private String letter;

    private String letterPronouns;

    private String alphabet;

    private String letterGroup;
}
