package com.example.english_learning.repository;

import com.example.english_learning.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    boolean existsByEmailAndPassword(String email, String password);

    boolean existsByEmail(String email);
}
