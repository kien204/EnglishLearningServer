package com.example.english_learning.repository.grammar;

import com.example.english_learning.models.GrammarGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrammarGroupRepository extends JpaRepository<GrammarGroup, Long> {
}
