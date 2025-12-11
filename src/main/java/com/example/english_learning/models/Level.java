package com.example.english_learning.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "levels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;  // ví dụ: A1, B2, C2

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String name;  // ví dụ: Beginner, Intermediate
}
