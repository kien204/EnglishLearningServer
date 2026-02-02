package com.example.english_learning.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "NVARCHAR(30)")
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    private String phone;
    private LocalDate birthday;
    @Column(columnDefinition = "NVARCHAR(10)")
    private String gender;
    @Column(columnDefinition = "NVARCHAR(50)")
    private String address;
    private String avatarUrl;
    private String role = "USER";
    @Column(name = "status")
    private long status = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
