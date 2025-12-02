package com.example.english_learning.repository.quiz;

import com.example.english_learning.models.GroupOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupOptionRepository extends JpaRepository<GroupOption, Long> {
    @Query("SELECT g.optionText FROM GroupOption g WHERE g.exercise.id = :exerciseId")
    List<String> findOptionTextByExerciseId(@Param("exerciseId") Long exerciseId);
}
