package com.example.english_learning.repository.quiz;

import com.example.english_learning.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByExercise_Id(Long exercise);
    
    @Query(value = "SELECT TOP (:limit) * FROM questions WHERE exercise_id = :exerciseId ORDER BY NEWID()", nativeQuery = true)
    List<Question> findRandomByExerciseId(@Param("exerciseId") Long exerciseId, @Param("limit") int limit);


}
