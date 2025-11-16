package com.example.english_learning.repository;

import com.example.english_learning.models.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabularyRepossitory extends JpaRepository<Vocabulary, Long> {
    boolean existsByWord(String id);

    Vocabulary findByWord(String word);
}
