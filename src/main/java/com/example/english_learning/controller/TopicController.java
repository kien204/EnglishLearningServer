package com.example.english_learning.controller;

import com.example.english_learning.dto.request.TopicRequest;
import com.example.english_learning.models.Topic;
import com.example.english_learning.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/topic")
public class TopicController {
    @Autowired
    private TopicService topicService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllTopics() {
        return topicService.getAll();
    }

    @GetMapping("/getById/{id}")
    public Topic getById(@PathVariable Long id) {
        return topicService.getById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTopic(@RequestBody TopicRequest request) {
        return topicService.createTopic(request);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTopic(@PathVariable Long id, @RequestBody TopicRequest request) {
        return topicService.updateTopic(id, request);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> deleteAllTopics() {
        return topicService.deleteAll();
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> deleteTopic(@PathVariable Long id) {
        return topicService.deleteById(id);
    }

    @PostMapping(
            value = "/upload",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "File trống"));
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không xác định được tên file"));
        }

        try {
            if (fileName.endsWith(".csv")) {
                topicService.importFromCsv(file);
            } else if (fileName.endsWith(".txt")) {
                topicService.importFromCsv(file);
            } else if (fileName.endsWith(".json")) {
                topicService.importFromJson(file);
            } else if (fileName.endsWith(".xlsx")) {
                topicService.importFromXlsx(file);
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("mes", "Định dạng không hỗ trợ. Chỉ hỗ trợ CSV, TXT, JSON, XLSX"));
            }

            return ResponseEntity.ok(Map.of("mes", "Import dữ liệu thành công"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mes", "Lỗi khi đọc file: " + e.getMessage()));
        }
    }
}
