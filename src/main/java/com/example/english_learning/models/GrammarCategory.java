package com.example.english_learning.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "grammar_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrammarCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private GrammarGroup group;

    @Column(nullable = false)
    private String title;
}
