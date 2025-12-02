package com.example.english_learning.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class QuizTreeResponse {
    private Long id;
    private String topic;
    private Integer groupWord;
    private String title;
    private int type;
    private String description;
    private String imageUrl;
    private String audioUrl;
    private List<String> groupOptionList;
    private List<SubQuestionNode> subQuestionNodes;

    @Data
    public static class SubQuestionNode {
        private Long id;
        private String questionText;
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
