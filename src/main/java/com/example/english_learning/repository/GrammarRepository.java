package com.example.english_learning.repository;

import com.example.english_learning.models.Grammar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrammarRepository extends JpaRepository<Grammar, Long> {
    List<Grammar> findAllByTopicId(Long topicId);
}
