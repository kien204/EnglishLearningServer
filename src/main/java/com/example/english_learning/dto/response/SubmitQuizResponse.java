package com.example.english_learning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitQuizResponse {
    private Long exerciseId;
    private Float score;
    private Long correctCount;
    private Long totalQuestions;
    private List<SelectQuestion> results;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SelectQuestion {
        private Long questionId;
        private Long selectedOptionId;
        private boolean isCorrect;
    }
}
