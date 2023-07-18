package com.project.japaneseBot.alphabet.repository;

import com.project.japaneseBot.alphabet.entity.KatakanaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface ReadOnlyKatakanaRepository extends JpaRepository<KatakanaEntity, Long> {

}
