package com.example.english_learning.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "grammar")
public class Grammar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "step_id")
    private LearningStep step;

    private String title;
    private String structure;
    private String explanation;
    private String example;
    private String tip;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
