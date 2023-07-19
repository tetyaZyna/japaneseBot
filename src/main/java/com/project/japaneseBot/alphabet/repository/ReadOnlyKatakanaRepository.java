package com.project.japaneseBot.alphabet.repository;

import com.project.japaneseBot.alphabet.entity.KatakanaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ReadOnlyKatakanaRepository extends JpaRepository<KatakanaEntity, Long> {
    boolean existsByHieroglyph(String hieroglyph);
    Optional<KatakanaEntity> findByHieroglyph(String hieroglyph);
    KatakanaEntity findByHieroglyphId(long hieroglyphId);

}
