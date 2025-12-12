package com.example.english_learning.dto.response;

import com.example.english_learning.models.Level;
import lombok.Data;

import java.util.List;

@Data
public class TopbarResponse {
    private Long skillId;
    private String name;
    private List<Level> levels;
}
