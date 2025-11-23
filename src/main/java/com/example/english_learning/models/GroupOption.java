package com.example.english_learning.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "group_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_option_id")
    private Long questionOptionId;

    @Column(name = "option_text", columnDefinition = "NVARCHAR(MAX)")
    private String optionText;
}
