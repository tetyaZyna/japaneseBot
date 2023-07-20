package com.project.japaneseBot.user.repository;

import com.project.japaneseBot.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByUserId(long userId);
    UserEntity findByUserId(long userId);
}
