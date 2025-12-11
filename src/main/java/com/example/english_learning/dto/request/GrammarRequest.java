package com.example.english_learning.dto.request;

import lombok.Data;

@Data
public class GrammarRequest {
    private Long topicId;
    private String title;
    private String structure;
    private String explanation;
    private String example;
    private String tip;
    private String imageUrl;
}
