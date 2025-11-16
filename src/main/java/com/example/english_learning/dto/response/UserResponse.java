package com.example.english_learning.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private Long id;

    private String email;

    private String fullName;

    private String role;

    public UserResponse(String email, String fullName, String role) {
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }
}