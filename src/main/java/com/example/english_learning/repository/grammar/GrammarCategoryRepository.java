package com.example.english_learning.repository.grammar;

import com.example.english_learning.models.GrammarCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrammarCategoryRepository extends JpaRepository<GrammarCategory, Long> {
    List<GrammarCategory> findByGroupId(Long groupId);
}


