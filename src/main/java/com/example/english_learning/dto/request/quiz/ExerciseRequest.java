package com.example.english_learning.dto.request.quiz;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ExerciseRequest {
    private Long skillId;
    private Long levelId;
    private Long topicId;
    private String title;
    private int type;
    private String description;
    private int ordering;
    private String audioUrl;

    private MultipartFile image;  // file ảnh
}