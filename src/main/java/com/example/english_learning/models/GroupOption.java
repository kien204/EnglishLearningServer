package com.example.english_learning.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JoinColumn(name = "exercise_id", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Exercise exercise;

    @Column(name = "option_text", columnDefinition = "NVARCHAR(MAX)")
    private String optionText;

}
