package com.example.english_learning.dto.request;

import lombok.Data;

@Data
public class SkillRequest {
    private String code;
    private String name;
    private String description;
    private Boolean visibleOnTopbar;
}
