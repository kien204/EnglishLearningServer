package com.example.english_learning.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_vocabulary_progress")
public class UserVocabularyProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "vocab_id")
    private Vocabulary vocabulary;

    private LocalDateTime learnedAt = LocalDateTime.now();
    private LocalDateTime lastReviewedAt;
    private LocalDateTime nextReviewAt;
    private Integer masteryLevel = 0;
    private Boolean isMastered = false;
}
