package com.example.english_learning.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "Email không được để trống")
    private String email;
    private String otp;
    private String newPassword;
}
