package com.example.english_learning.repository.quiz;

import com.example.english_learning.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByExercise_Id(Long exercise);
}
