package com.example.english_learning.repository.quiz;

import com.example.english_learning.models.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    public List<Exercise> findByLevelIdAndSkillIdAndTopicId(Long levelId, Long skillId, Long topicId);
}
