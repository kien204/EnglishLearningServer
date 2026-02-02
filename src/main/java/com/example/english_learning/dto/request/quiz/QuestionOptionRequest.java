package com.example.english_learning.dto.request.quiz;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuestionOptionRequest {
    @NotNull
    private Long questionId;
    private String optionText;
    private Boolean isCorrect;
}