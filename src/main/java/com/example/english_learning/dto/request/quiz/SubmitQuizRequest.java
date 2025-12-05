package com.example.english_learning.dto.request.quiz;

import lombok.Data;

import java.util.List;

@Data
public class SubmitQuizRequest {
    private Long exerciseId;
    private List<Answer> answers;

    @Data
    public static class Answer {
        private Long questionId;
        private Long selectedOptionId;
    }
}
