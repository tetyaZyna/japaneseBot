package com.project.japaneseBot.user.entity;

import com.project.japaneseBot.task.entity.TaskEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Validated
@Table(name = "\"user\"")
public class UserEntity {
    @Id
    private Long userId;

    private LocalDate registrationDate;

    private String mode;

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TaskEntity> tasks;
}
