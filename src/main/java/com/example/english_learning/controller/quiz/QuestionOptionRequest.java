package com.example.english_learning.controller.quiz;

import lombok.Data;

@Data
public class QuestionOptionRequest {
    private Long questionId;
    private String optionText;
    private Boolean isCorrect;
    private String correct;
}