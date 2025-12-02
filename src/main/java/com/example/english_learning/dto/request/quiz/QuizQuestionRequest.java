package com.example.english_learning.dto.request.quiz;

import lombok.Data;

import java.util.List;

@Data
public class QuizQuestionRequest {
    private Long TopicId;
    private Long vocabularyId;
    private Long grammarId;
    private Long exerciseId;
    private String question_text;
    private String corrrect;
    private Integer groupWord; // ví dụ tiếng Việt
    private Integer ordering;
    private List<OptionRequest> options;

    @Data
    public class OptionRequest {
        private String option_text;
        private Boolean is_correct;
    }

}
