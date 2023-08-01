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
    
    private Integer questionCount;

    @NotBlank
    private String alphabet;

    @NotBlank
    private String letterGroup;

    private Boolean useLetters;

    private Boolean usePronouns;

    @OneToMany(mappedBy = "taskSettingsEntity", fetch = FetchType.EAGER)
    private List<TaskEntity> taskEntity;

}
