package com.example.english_learning.dto.request.quiz;

import lombok.Data;

@Data
public class WritingRequest {
    private Long exerciseId;
    private String transcript;
}
