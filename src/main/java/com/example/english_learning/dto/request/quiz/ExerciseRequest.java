package com.example.english_learning.dto.request.quiz;

import lombok.Data;

@Data
public class ExerciseRequest {
    private Long topicId;
    private Integer groupWord;
    private String title;
    private int type;
    private String description;
}
