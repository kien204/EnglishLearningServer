package com.example.english_learning.repository;

import com.example.english_learning.models.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
    boolean existsByWord(String id);

    boolean existsByPos(String pos);

    List<Vocabulary> findByTopic_Id(Long id);

    Vocabulary findByWord(String word);
}
