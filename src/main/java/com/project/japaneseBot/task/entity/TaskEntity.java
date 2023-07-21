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
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long taskId;
    private int questionNumber;
    private int questionCount;
    @ElementCollection
    @CollectionTable(name = "task_letters", joinColumns = @JoinColumn(name = "task_id"))
    @MapKeyColumn(name = "letter_key")
    @Column(name = "letter_value")
    private Map<String, String> letterAndPronounseMap;
    @ElementCollection
    @CollectionTable(name = "user_answers", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "is_answer_correct")
    private List<Boolean> isAnswerCorrect;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;
}
