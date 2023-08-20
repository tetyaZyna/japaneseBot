package com.project.japaneseBot.alphabet.repository;

import com.project.japaneseBot.alphabet.model.entity.AlphabetsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@SuppressWarnings("unused")
public interface AlphabetsRepository extends JpaRepository<AlphabetsEntity, Long> {
    List<AlphabetsEntity> findByAlphabetAndLetterGroup(String alphabet, String letterGroup);
    List<AlphabetsEntity> findByLetterGroup(String letterGroup);
    Optional<AlphabetsEntity> findByAlphabetAndLetterPronounsIgnoreCase(String alphabet, String letterPronouns);
    Optional<AlphabetsEntity> findByLetterPronouns(String letterPronouns);
    boolean existsByLetterPronouns(String letterPronouns);
    boolean existsByLetter(String letter);
    Optional<AlphabetsEntity> findByLetter(String letter);
    List<AlphabetsEntity> findByAlphabet(String alphabet);
    Optional<AlphabetsEntity> findByLetterId(Long letterId);
}