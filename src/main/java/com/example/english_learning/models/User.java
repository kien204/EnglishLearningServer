package com.example.english_learning.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    private String phone;
    private String role = "USER";
    @Column(name = "is_activate")
    private boolean isActivate = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
