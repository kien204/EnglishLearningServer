package com.example.english_learning.dto.request;

import lombok.Data;

@Data
public class TopicRequest {
    private Long skillId;
    private Long levelId;
    private String name;
    private String description;
    private String imageUrl;
}
