package com.example.english_learning.controller.quiz;

import com.example.english_learning.dto.request.quiz.GroupOptionRequest;
import com.example.english_learning.service.quiz.GroupOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group-options")
public class GroupOptionController {
    @Autowired
    private GroupOptionService groupOptionService;

    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        return groupOptionService.getAll();
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return groupOptionService.getById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createGroupOption(@RequestBody GroupOptionRequest request) {
        return groupOptionService.saveGroup(request);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateGroupOption(@PathVariable Long id, @RequestBody GroupOptionRequest request) {
        return groupOptionService.updateGroup(id, request);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> deleteAllGroupOption() {
        return groupOptionService.deleteAll();
    }

    @DeleteMapping("deteteById/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        return groupOptionService.deleteGroup(id);
    }
}
