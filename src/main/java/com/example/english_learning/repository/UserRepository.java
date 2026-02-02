package com.example.english_learning.repository;

import com.example.english_learning.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Long countByStatus(Integer status);

    boolean existsByEmail(String email);
}
