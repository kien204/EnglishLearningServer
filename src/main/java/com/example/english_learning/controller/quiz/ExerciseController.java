package com.example.english_learning.controller.quiz;

import com.example.english_learning.dto.request.quiz.ExerciseRequest;
import com.example.english_learning.models.Exercise;
import com.example.english_learning.service.quiz.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/exercise")
public class ExerciseController {
    @Autowired
    private ExerciseService exerciseService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllExercises() {
        return exerciseService.getAll();
    }

    @GetMapping("/getByTopic/{topicId}")
    public ResponseEntity<?> getByTopic(@PathVariable Long topicId) {
        return exerciseService.getByTopic(topicId);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return exerciseService.findById(id);
    }

    @GetMapping("/getExercisePage")
    public Object getVocabularies(@RequestParam int page, @RequestParam int size) {
        Page<Exercise> pageData = exerciseService.getExercisePage(page - 1, size);

        return ResponseEntity.ok(Map.of(
                "data", pageData.getContent(),
                "currentPage", pageData.getNumber(),
                "totalItems", pageData.getTotalElements(),
                "totalPages", pageData.getTotalPages()
        ));
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createExercise(
            @RequestParam(value = "topicId", required = false) Long topicId,
            @RequestParam(value = "groupWord", required = false) Integer groupWord,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "audio", required = false) MultipartFile audio
    ) {

        ExerciseRequest request = new ExerciseRequest();
        request.setTopicId(topicId);
        request.setGroupWord(groupWord);
        request.setTitle(title);
        request.setType(type);
        request.setDescription(description);

        return exerciseService.create(request, image, audio);
    }


    @PutMapping(value = "/update/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateExercise(@PathVariable Long id, @RequestParam(value = "topicId", required = false) Long topicId,
                                            @RequestParam(value = "groupWord", required = false) Integer groupWord,
                                            @RequestParam(value = "title", required = false) String title,
                                            @RequestParam(value = "type") int type,
                                            @RequestParam(value = "description", required = false) String description,
                                            @RequestParam(value = "image", required = false) MultipartFile image,
                                            @RequestParam(value = "audio", required = false) MultipartFile audio
    ) {

        ExerciseRequest request = new ExerciseRequest();
        request.setTopicId(topicId);
        request.setGroupWord(groupWord);
        request.setTitle(title);
        request.setType(type);
        request.setDescription(description);
        return exerciseService.update(id, request, image, audio);
    }


    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> deleteAllExercises() {
        return exerciseService.deleteAll();
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> deleteExerciseById(@PathVariable Long id) {
        return exerciseService.deleteById(id);
    }
}
