package com.example.english_learning.repository.quiz;

import com.example.english_learning.models.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {
    List<QuestionOption> findByQuestion_Id(Long questionId);

    @Query("""
                SELECT o.id 
                FROM QuestionOption o 
                WHERE o.isCorrect = true 
                  AND o.question.exercise.id = :exerciseId
            """)
    List<Long> findCorrectOptionIdsByExercise(Long exerciseId);


}
