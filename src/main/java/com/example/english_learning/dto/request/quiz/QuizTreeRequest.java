package com.example.english_learning.dto.request.quiz;

import lombok.Data;

import java.util.List;

@Data
public class QuizTreeRequest {
    private Long skillId;
    private Long levelId;
    private Long topicId;
    private String title;
    private int type;
    private String imageUrl;
    private String audioUrl;
    private int ordering;
    private List<String> groupOptionList;
    private List<QuizTreeRequest.SubQuestionNode> subQuestionNodes;

    @Data
    public static class SubQuestionNode {
        private String questionText;
        private int ordering;
        private String correct;
        private List<QuizTreeRequest.SubQuestionNode.SubOptionNode> options;

        @Data
        public static class SubOptionNode {
            private String optionText;
            private Boolean IsCorrect;
        }
    }
}
