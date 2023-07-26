package com.project.japaneseBot.alphabet.repository;

import com.project.japaneseBot.alphabet.model.entity.KatakanaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Deprecated
public interface ReadOnlyKatakanaRepository extends JpaRepository<KatakanaEntity, Long> {
    Optional<KatakanaEntity> findByHieroglyphPronouns(String hieroglyphPronouns);
    boolean existsByHieroglyphPronouns(String hieroglyphPronouns);
    boolean existsByHieroglyph(String hieroglyph);
    Optional<KatakanaEntity> findByHieroglyph(String hieroglyph);
    Optional<KatakanaEntity> findByHieroglyphId(long hieroglyphId);

}
