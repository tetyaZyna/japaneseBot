package com.project.japaneseBot.user.repository;

import com.project.japaneseBot.task.model.entity.TaskEntity;
import com.project.japaneseBot.user.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByUserId(long userId);
    UserEntity findByUserId(long userId);
}
