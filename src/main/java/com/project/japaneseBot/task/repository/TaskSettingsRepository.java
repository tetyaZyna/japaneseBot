package com.project.japaneseBot.task.repository;

import com.project.japaneseBot.task.model.entity.TaskSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskSettingsRepository extends JpaRepository<TaskSettingsEntity, Long> {
    TaskSettingsEntity findBySettingsName(String settingsName);
}
