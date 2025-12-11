package com.example.english_learning.service.quiz;

import com.example.english_learning.dto.request.quiz.ExerciseRequest;
import com.example.english_learning.models.Exercise;
import com.example.english_learning.models.Topic;
import com.example.english_learning.repository.LevelRepository;
import com.example.english_learning.repository.SkillRepository;
import com.example.english_learning.repository.TopicRepository;
import com.example.english_learning.repository.quiz.ExerciseRepository;
import com.example.english_learning.service.other.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

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

    public ResponseEntity<?> create(ExerciseRequest req, MultipartFile image, MultipartFile audio) {

        Topic topic = topicRepository.findById(req.getTopicId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Topic"));


        String imageUrl = null;
        String audioUrl = null;

        if (image != null && !image.isEmpty()) {
            imageUrl = cloudinaryService.uploadFile(image);
        }

        if (audio != null && !audio.isEmpty()) {
            audioUrl = cloudinaryService.uploadFile(audio);
        }

        Exercise exercise = Exercise.builder()
                .topic(topic)
                .groupWord(req.getGroupWord())
                .title(req.getTitle())
                .type(req.getType())
                .description(req.getDescription())
                .imageUrl(imageUrl)
                .audioUrl(audioUrl)
                .build();

        exerciseRepository.save(exercise);

        return ResponseEntity.ok(exercise);
    }


    public ResponseEntity<?> update(Long id, ExerciseRequest req, MultipartFile image, MultipartFile audio) {
        Optional<Exercise> optional = exerciseRepository.findById(id);

        Topic topic = topicRepository.findById(req.getTopicId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Topic"));


        if (optional.isEmpty()) {
            return ResponseEntity.badRequest().body("Exercise không tồn tại");
        }

        Exercise exercise = optional.get();

        // --- HANDLE IMAGE ---
        if (image != null && !image.isEmpty()) {
            String uploadedImage = cloudinaryService.uploadFile(image);
            exercise.setImageUrl(uploadedImage);
        }

        // --- HANDLE AUDIO ---
        if (audio != null && !audio.isEmpty()) {
            String uploadedAudio = cloudinaryService.uploadFile(audio);
            exercise.setAudioUrl(uploadedAudio);
        }

        // --- UPDATE OTHER FIELDS ---
        exercise.setTopic(topic);
        exercise.setGroupWord(req.getGroupWord());
        exercise.setTitle(req.getTitle());
        exercise.setType(req.getType());
        exercise.setDescription(req.getDescription());

        exerciseRepository.save(exercise);
        return ResponseEntity.ok(exercise);
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

