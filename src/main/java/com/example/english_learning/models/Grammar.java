package com.example.english_learning.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "grammar")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grammar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = true)
    // @JsonIgnoreProperties("group")
    @JsonBackReference
    private Topic topic;

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
