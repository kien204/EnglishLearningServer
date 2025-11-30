package com.example.english_learning.dto.request.quiz;

import lombok.Data;

@Data
public class GroupOptionRequest {
    private String optionText;
    private Long exerciseId;
}
