package com.project.japaneseBot.user.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.japaneseBot.task.entity.TaskEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

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
    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<TaskEntity> tasks = new LinkedList<>();
}
