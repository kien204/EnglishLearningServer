package com.example.english_learning.controller;

import com.example.english_learning.dto.request.LevelResquest;
import com.example.english_learning.models.Level;
import com.example.english_learning.service.LevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/level")
public class LevelController {
    @Autowired
    private LevelService levelService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllTopics() {
        return levelService.getAll();
    }

    @GetMapping("/getById/{id}")
    public Level getById(@PathVariable Long id) {
        return levelService.getById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createLevel(@RequestBody LevelResquest request) {
        return levelService.createLevel(request);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateLevel(@PathVariable Long id, @RequestBody LevelResquest request) {
        return levelService.updateLevel(id, request);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> delteAllLevel() {
        return levelService.deleteAll();
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        return levelService.deleteById(id);
    }
}
