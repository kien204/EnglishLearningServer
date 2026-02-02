package com.example.english_learning.service;

import com.example.english_learning.repository.TopicRepository;
import com.example.english_learning.repository.UserRepository;
import com.example.english_learning.repository.quiz.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private ExerciseRepository exerciseRepository;

    public ResponseEntity<?> getDashboardStats() {
        long userCount = userRepository.count();
        long topicCount = topicRepository.count();
        long exerciseCount = exerciseRepository.count();

        var stats = new java.util.HashMap<String, Long>();
        stats.put("userCount", userCount);
        stats.put("topicCount", topicCount);
        stats.put("exerciseCount", exerciseCount);

        return ResponseEntity.ok(stats);
    }

    public ResponseEntity<?> getUserStats() {
        long userCount = userRepository.count();
        long userActiveCount = userRepository.countByStatus(1);
        long userLockedCount = userRepository.countByStatus(2);
        long userNotACtivedCount = userRepository.countByStatus(0);

        var stats = new java.util.HashMap<String, Long>();
        stats.put("userCount", userCount);
        stats.put("userActiveCount", userActiveCount);
        stats.put("userLockedCount", userLockedCount);
        stats.put("userNotACtivedCount", userNotACtivedCount);
        return ResponseEntity.ok(stats);
    }
}
