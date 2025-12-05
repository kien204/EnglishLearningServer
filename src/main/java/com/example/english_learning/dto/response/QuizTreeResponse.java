package com.example.english_learning.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class QuizTreeResponse {
    private Long exerciseId;
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
        private Long questionId;
        private String questionText;
        private List<SubOptionNode> options;

        @Data
        public static class SubOptionNode {
            private Long optionId;
            private String optionText;
        }
    }
}
