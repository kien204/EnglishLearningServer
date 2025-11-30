package com.example.english_learning.dto.request;

import lombok.Data;

@Data
public class VocabularyRequest {
    private String word;
    private String pos;
    private String pron;
    private String meaningVn;
    private String v2;
    private String v3;
    private Long levelId;
    private Long topicId;
    private String exampleEn;
    private String exampleVn;
    private Integer groupWord;
}
