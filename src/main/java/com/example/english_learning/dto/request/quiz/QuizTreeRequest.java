package com.example.english_learning.dto.request.quiz;

import lombok.Data;

import java.util.List;

@Data
public class QuizTreeRequest {
    private Long topicId;
    private Integer groupWord;
    private String title;
    private int type;
    private String imageUrl;
    private String audioUrl;
    private List<String> groupOptionList;
    private List<QuizTreeRequest.SubQuestionNode> subQuestionNodes;

    @Data
    public static class SubQuestionNode {
        private String questionText;
        private String correct;
        private List<QuizTreeRequest.SubQuestionNode.SubOptionNode> options;

        @Data
        public static class SubOptionNode {
            private String optionText;
            private Boolean IsCorrect;
        }
    }
}
