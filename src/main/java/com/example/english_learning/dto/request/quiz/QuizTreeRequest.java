package com.example.english_learning.dto.request.quiz;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuizTreeRequest {
    private Long topicId;
    private Integer groupWord;
    private String title;
    private int type;
    private String description;
    private String imageUrl;
    private String audioUrl;
    private List<QuizTreeRequest.SubQuestionNode> subQuestionNodes;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SubQuestionNode {
        private String questionText;
        private Long vocabulary_id;
        private List<QuizTreeRequest.SubQuestionNode.SubOptionNode> options;

        @Data
        public static class SubOptionNode {
            private String optionText;
            private Boolean isCorrect;
        }
    }
}
