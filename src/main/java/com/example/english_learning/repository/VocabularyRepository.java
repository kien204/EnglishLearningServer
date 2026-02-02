package com.example.english_learning.repository;

import com.example.english_learning.models.Topic;
import com.example.english_learning.models.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
    boolean existsByWordAndPos(String word, String pos);


    List<Vocabulary> findByTopic_Id(Long id);

    List<Vocabulary> findByGroupWord(Integer groupWord);

    @Query("SELECT DISTINCT v.groupWord FROM Vocabulary v WHERE v.topic = :topic ORDER BY v.groupWord ASC")
    List<Integer> findGroupWordByTopic(@Param("topic") Topic topic);


    Vocabulary findByWord(String word);
}
