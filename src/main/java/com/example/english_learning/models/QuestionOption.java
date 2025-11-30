package com.example.english_learning.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = true)
    private Question question;

    @Column(name = "option_text", columnDefinition = "NVARCHAR(MAX)")
    private String optionText;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @JsonProperty("question")
    public Long getQuestionId() {
        return question != null ? question.getId() : null;
    }
}
