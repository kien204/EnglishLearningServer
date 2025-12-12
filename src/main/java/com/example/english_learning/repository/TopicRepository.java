package com.example.english_learning.repository;

import com.example.english_learning.models.Level;
import com.example.english_learning.models.Skill;
import com.example.english_learning.models.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findBySkill_Id(Long id);

    List<Topic> findBySkill_IdAndLevel_Id(Long skillId, Long levelId);

    @Query("""
                SELECT t.skill
                FROM Topic t
                WHERE t.skill IS NOT NULL
                  AND t.skill.visibleOnTopbar = true
                GROUP BY t.skill
            """)
    List<Skill> getTopbarSkills();


    @Query("""
                SELECT DISTINCT t.level
                FROM Topic t
                WHERE t.skill.id = :skillId
                  AND t.level IS NOT NULL
            """)
    List<Level> getLevelsBySkillId(Long skillId);


}
