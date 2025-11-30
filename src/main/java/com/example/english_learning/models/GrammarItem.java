package com.example.english_learning.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "grammar_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrammarItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // category_id
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    // @JsonIgnoreProperties("group")
    @JsonBackReference
    private GrammarCategory category;

    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String title;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String structure;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String explanation;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String example;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String tip;

    private String imageUrl;
}
