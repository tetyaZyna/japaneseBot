package com.project.japaneseBot.alphabet.repository;

import com.project.japaneseBot.alphabet.entity.HiraganaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ReadOnlyHiraganaRepository extends JpaRepository<HiraganaEntity, Long> {
    Optional<HiraganaEntity> findByHieroglyph(String hieroglyph);
    boolean existsByHieroglyph(String hieroglyph);
    HiraganaEntity findByHieroglyphId(long hieroglyphId);

}