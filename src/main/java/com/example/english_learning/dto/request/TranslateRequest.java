package com.example.english_learning.dto.request;

import lombok.Data;

@Data
public class TranslateRequest {
    private String text;
    private String source_lang = "en";
    private String target_lang = "vi";
}
