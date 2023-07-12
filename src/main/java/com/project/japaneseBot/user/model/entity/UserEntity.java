package com.project.japaneseBot.user.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

import static jakarta.persistence.GenerationType.IDENTITY;
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
}
