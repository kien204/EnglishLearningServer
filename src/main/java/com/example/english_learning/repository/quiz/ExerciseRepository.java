package com.example.english_learning.repository.quiz;

import com.example.english_learning.models.Exercise;
import com.example.english_learning.models.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByTopicAndGroupWord(Topic topic, Integer groupWord);

    List<Exercise> findByGroupWord(int id);

}
