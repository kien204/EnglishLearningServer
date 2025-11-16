package com.example.english_learning.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "learning_step")
public class LearningStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String level;

    @Column(name = "order_index")
    private Integer orderIndex = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // 1-n
    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL)
    private List<Vocabulary> vocabularies;

    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL)
    private List<Grammar> grammars;

    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL)
    private List<QuizQuestion> quizQuestions;
}
