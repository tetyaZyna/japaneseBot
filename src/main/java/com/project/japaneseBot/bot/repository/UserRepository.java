package com.project.japaneseBot.bot.repository;

import com.project.japaneseBot.user.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
