package com.example.english_learning.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRequest {
    private String name;
    private String phone;
    private LocalDate birthday;
    private String gender;
    private String address;
    private String avatarUrl;
}
