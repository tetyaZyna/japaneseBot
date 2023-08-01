package com.project.japaneseBot.task.repository;

import com.project.japaneseBot.task.model.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    long countByUserEntity_UserId(Long userId);
    Optional<TaskEntity> findFirstByUserEntity_UserIdOrderByTaskIdDesc(long userId);
}
