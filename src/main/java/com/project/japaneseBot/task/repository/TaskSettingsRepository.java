package com.project.japaneseBot.task.repository;

import com.project.japaneseBot.task.entity.TaskEntity;
import com.project.japaneseBot.task.entity.TaskSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskSettingsRepository extends JpaRepository<TaskSettingsEntity, Long> {
    TaskSettingsEntity findBySettingsName(String settingsName);
}
