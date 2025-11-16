package com.example.english_learning.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String message;  // thông báo
    private String token;    // JWT token
    private Object user;     // thông tin user (optional)
}
