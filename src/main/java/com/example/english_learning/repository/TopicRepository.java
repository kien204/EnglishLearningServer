package com.example.english_learning.repository;

import com.example.english_learning.models.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}
