package com.project.japaneseBot.task.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Validated
@Table(name = "task_letters")
public class TaskLettersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskEntity taskEntity;

    private String letterKey;

    private String keyType;

    private String letterValue;

    private String letterAlphabet;
}
