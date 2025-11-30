package com.example.english_learning.controller.quiz;

import com.example.english_learning.dto.request.quiz.ExerciseRequest;
import com.example.english_learning.service.quiz.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exercise")
public class ExerciseController {
    @Autowired
    private ExerciseService exerciseService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllExercises() {
        return exerciseService.getAll();
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return exerciseService.findById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createExercise(@RequestBody ExerciseRequest request) {
        return exerciseService.create(request);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateExercise(@PathVariable Long id, @RequestBody ExerciseRequest request) {
        return exerciseService.update(id, request);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> deleteAllExercises() {
        return exerciseService.deleteAll();
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> deleteExerciseById(Long id) {
        return exerciseService.deleteById(id);
    }
}
