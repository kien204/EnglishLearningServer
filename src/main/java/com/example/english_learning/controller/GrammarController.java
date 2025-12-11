package com.example.english_learning.controller;

import com.example.english_learning.dto.request.GrammarRequest;
import com.example.english_learning.service.GrammarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/grammarItem")
public class GrammarController {

    @Autowired
    private GrammarService grammarItemService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody GrammarRequest request) {
        return grammarItemService.create(request);
    }

    @PostMapping("/create/withJson")
    public ResponseEntity<?> create(@RequestBody List<GrammarRequest> request) {
        return grammarItemService.createWithJson(request);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody GrammarRequest request
    ) {
        return grammarItemService.update(id, request);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        return grammarItemService.getAlḷ();
    }

    @GetMapping("/getByTopic/{topicId}")
    public ResponseEntity<?> getByTopic(@PathVariable Long topicId) {
        return grammarItemService.getByTopicId(topicId);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return grammarItemService.delete(id);
    }

    @DeleteMapping("/delete/all")
    public ResponseEntity<?> delete() {
        return grammarItemService.deleteAll();
    }

    @PostMapping(
            value = "/upload",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "File trống"));
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không xác định được tên file"));
        }

        try {
            if (fileName.endsWith(".json")) {
                return grammarItemService.importFromJson(file);

            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Định dạng không hỗ trợ. Chỉ CSV, TXT, JSON, XLSX."));
            }

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Lỗi khi đọc file: " + e.getMessage()));
        }
    }
}

