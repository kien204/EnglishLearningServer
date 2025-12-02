package com.example.english_learning.service.quiz;

import com.example.english_learning.dto.request.quiz.ExerciseRequest;
import com.example.english_learning.models.Exercise;
import com.example.english_learning.models.Topic;
import com.example.english_learning.repository.LevelRepository;
import com.example.english_learning.repository.SkillRepository;
import com.example.english_learning.repository.TopicRepository;
import com.example.english_learning.repository.quiz.ExerciseRepository;
import com.example.english_learning.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class ExerciseService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    public ResponseEntity<?> create(ExerciseRequest request) {
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Topic"));

        String imageUrl = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imageUrl = cloudinaryService.uploadFile(request.getImage());
        }


        Exercise exercise = Exercise.builder()
                .topic(topic)
                .groupWord(request.getGroupWord())
                .title(request.getTitle())
                .type(request.getType())
                .description(request.getDescription())
                .imageUrl(imageUrl)
                .audioUrl(request.getAudioUrl())
                .build();

        exerciseRepository.save(exercise);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Tạo bài tập thành công"));
    }

    public ResponseEntity<?> update(Long id, ExerciseRequest request) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Exercise"));

        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Topic"));

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            exercise.setImageUrl(cloudinaryService.uploadFile(request.getImage()));
        }

        exercise.setTopic(topic);
        exercise.setGroupWord(request.getGroupWord());
        exercise.setTitle(request.getTitle());
        exercise.setType(request.getType());
        exercise.setDescription(request.getDescription());
        exercise.setAudioUrl(request.getAudioUrl());

        exerciseRepository.save(exercise);

        return ResponseEntity.ok(Map.of("message", "Cập nhật bài tập thành công"));
    }

    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(exerciseRepository.findAll());
    }

    public ResponseEntity<?> findById(Long id) {
        Exercise e = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Exercise"));
        return ResponseEntity.ok(e);
    }

    public ResponseEntity<?> deleteAll() {
        exerciseRepository.deleteAll();
        return ResponseEntity.ok(Map.of("message", "Xóa tất cả bài tập thành công"));
    }

    public ResponseEntity<?> deleteById(Long id) {
        Exercise e = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Exercise"));
        exerciseRepository.delete(e);
        return ResponseEntity.ok(Map.of("message", "Xóa bài tập thành công"));
    }
}

