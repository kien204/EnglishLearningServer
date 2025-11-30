package com.example.english_learning.controller;

import com.example.english_learning.dto.request.ResetPasswordRequest;
import com.example.english_learning.dto.request.auth.EmailRequest;
import com.example.english_learning.dto.request.auth.LoginRequest;
import com.example.english_learning.dto.request.auth.RegisterRequest;
import com.example.english_learning.dto.response.LoginResponse;
import com.example.english_learning.models.User;
import com.example.english_learning.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/send-otp")
    public String sendOtp(@RequestBody EmailRequest emailRequest) {
        return authService.sendOtp(emailRequest.getEmail());
    }

    @PutMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        return authService.resetPassword(resetPasswordRequest);
    }

    @PostMapping("/resend-otp")
    public String resendOtp(@RequestBody EmailRequest emailRequest) {
        return authService.resendOtp(emailRequest.getEmail());
    }

    @PutMapping("/activate-account/{email}/{otp}")
    public ResponseEntity<?> activateAccount(@PathVariable("email") String email, @PathVariable("otp") String otp) {
        return authService.activateUser(email, otp);
    }

    @Operation(summary = "Cập nhật vai trò tài khoản")
    @PutMapping("/updateRole/{id}")
    public Object updateRole(@PathVariable int id, @RequestParam String role) {
        return authService.updateRole(id, role);
    }

    @GetMapping("/getbyid/{id}")
    public User getUserById(@PathVariable int id) {
        return authService.getUserById(id);
    }

    @GetMapping("/all")
    public List<User> getAllUser() {
        return authService.getUserAll();
    }

    // ==================== DELETE USER BY ID (ADMIN ONLY) ====================
    @DeleteMapping("/{id}")
    public String deleteUserById(@PathVariable int id) {
        return authService.deleteUserById(id);
    }
}
