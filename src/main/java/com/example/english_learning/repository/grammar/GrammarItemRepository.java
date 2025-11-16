package com.example.english_learning.repository.grammar;

import com.example.english_learning.models.GrammarItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrammarItemRepository extends JpaRepository<GrammarItem, Long> {
    List<GrammarItem> findByCategoryId(Long categoryId);

}
