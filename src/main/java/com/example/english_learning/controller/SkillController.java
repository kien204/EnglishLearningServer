package com.example.english_learning.controller;

import com.example.english_learning.dto.request.SkillRequest;
import com.example.english_learning.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/skill")
public class SkillController {
    @Autowired
    private SkillService skillService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllTopics() {
        return skillService.getAll();
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return skillService.getById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTopic(@RequestBody SkillRequest request) {
        return skillService.createSkill(request);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTopic(@PathVariable Long id, @RequestBody SkillRequest request) {
        return skillService.updateSkill(id, request);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> deleteAllTopics() {
        return skillService.deleteAll();
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> deleteSkill(@PathVariable Long id) {
        return skillService.deleteSkill(id);
    }
}
