package com.project.japaneseBot.task.entity;

import com.project.japaneseBot.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Validated
@Table(name = "task")
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    private Integer questionNumber;

    private Integer questionCount;

    @OneToMany(mappedBy = "taskEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TaskLettersEntity> letters;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_answers", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "is_answer_correct")
    private List<Boolean> isAnswerCorrect;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;
}
