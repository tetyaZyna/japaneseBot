package com.project.japaneseBot.alphabet.repository;

import com.project.japaneseBot.alphabet.entity.HiraganaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Deprecated
public interface ReadOnlyHiraganaRepository extends JpaRepository<HiraganaEntity, Long> {
    Optional<HiraganaEntity> findByHieroglyphPronouns(String hieroglyphPronouns);
    boolean existsByHieroglyphPronouns(String hieroglyphPronouns);
    Optional<HiraganaEntity> findByHieroglyph(String hieroglyph);
    boolean existsByHieroglyph(String hieroglyph);
    Optional<HiraganaEntity> findByHieroglyphId(long hieroglyphId);

}