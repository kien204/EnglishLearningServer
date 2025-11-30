package com.example.english_learning.dto.request.quiz;

import lombok.Data;

@Data
public class QuestionRequest {
    private Long skillId;
    private Long levelId;
    private Long topicId;
    private Long vocabularyId;
    private Long grammarId;
    private Long exerciseId;
    private String questionText;
    private String correct;
    private int ordering;
}
