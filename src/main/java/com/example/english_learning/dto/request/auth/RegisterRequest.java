package com.example.english_learning.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String name;
    @NotBlank(message = "Email không được để trống")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@gmail\\.com$",
            message = "Email phải có định dạng @gmail.com"
    )
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String phone;
}
