package com.example.english_learning.dto.request;

import lombok.Data;

@Data
public class VocabularyRequest {
    private String word;
    private String wordType;   // trước là enum
    private String phonetic;
    private String meaning;
    private String v2;
    private String v3;
    private String level;      // trước là enum Level
    private String topic;
    private String audioUrl;
    private String example;
}
