package com.example.english_learning.dto.request.grammar;

import lombok.Data;

@Data
public class GrammarItemRequest {
    private Long categoryId;
    private String title;
    private String structure;
    private String explanation;
    private String example;
    private String tip;
    private String imageUrl;
}
