package com.project.japaneseBot.user.repository;

import com.project.japaneseBot.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
