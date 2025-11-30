package com.example.english_learning.dto.request.quiz;

import lombok.Data;

@Data
public class QuestionOptionRequest {
    private Long questionId;
    private String optionText;
    private Boolean isCorrect;
}