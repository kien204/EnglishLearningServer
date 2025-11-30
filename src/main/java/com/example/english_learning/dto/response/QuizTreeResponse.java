package com.example.english_learning.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class QuizTreeResponse {
    private Long id;
    private String skill;
    private String level;
    private String topic;
    private String title;
    private int type;
    private String description;
    private String imageUrl;
    private String audioUrl;
    private int ordering;
    private List<String> groupOptionList;
    private List<SubQuestionNode> subQuestionNodes;

    @Data
    public static class SubQuestionNode {
        private Long id;
        private String questionText;
        private int ordering;
        private String correct;
        private List<SubOptionNode> options;

        @Data
        public static class SubOptionNode {
            private Long id;
            private String optionText;
            private Boolean IsCorrect;
        }
    }
}
