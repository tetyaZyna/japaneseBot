package com.project.japaneseBot.alphabet.repository;

import com.project.japaneseBot.alphabet.model.entity.AlphabetsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlphabetsRepository extends JpaRepository<AlphabetsEntity, Long> {
    boolean existsByLetter(String letter);
    Optional<AlphabetsEntity> findByLetter(String letter);
    List<AlphabetsEntity> findByAlphabet(String alphabet);
    Optional<AlphabetsEntity> findByLetterId(Long letterId);
    long countByAlphabet(String alphabet);
}