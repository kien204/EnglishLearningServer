package com.example.english_learning.mapper;

import com.example.english_learning.dto.request.auth.RegisterRequest;
import com.example.english_learning.models.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {
    public User toEntity(RegisterRequest registerRequest) {
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setName(registerRequest.getName());
        user.setPhone(registerRequest.getPhone());
        return user;
    }
}
