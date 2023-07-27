package com.project.japaneseBot.task.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Validated
@Table(name = "task_settings")
public class TaskSettingsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long settingsId;

    @NotBlank
    private String settingsName;

    @NotBlank
    private Integer questionCount;

    @NotBlank
    private String alphabet;

    @NotBlank
    private String letterGroup;

    @NotBlank
    private Boolean useLetters;

    @NotBlank
    private Boolean usePronouns;

    @OneToMany(mappedBy = "taskSettingsEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TaskEntity> taskEntity;

}
