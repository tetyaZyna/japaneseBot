package com.project.japaneseBot.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Validated
public class UserEntity {
    @Id
    private long userId;
    private LocalDate registrationDate;
    private String mode;
}
