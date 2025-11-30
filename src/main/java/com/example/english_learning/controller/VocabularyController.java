package com.example.english_learning.controller;

import com.example.english_learning.dto.request.VocabularyRequest;
import com.example.english_learning.models.Vocabulary;
import com.example.english_learning.service.VocabularyService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/vocabulary")
public class VocabularyController {
    @Autowired
    private VocabularyService vocabularyService;

    @Operation(
            summary = "Tạo từ vựng mới",
            description = "API cho phép admin tạo một từ vựng mới"
    )
    @PostMapping("/createVocabulary")
    public ResponseEntity<?> createVocabulary(@RequestBody VocabularyRequest request) {
        return vocabularyService.createVocabulary(request);
    }

    @PostMapping(
            value = "/upload",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("mes", "File trống"));
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return ResponseEntity.badRequest().body(Map.of("mes", "Không xác định được tên file"));
        }

        try {
            if (fileName.endsWith(".csv")) {
                vocabularyService.importFromCsv(file);
            } else if (fileName.endsWith(".json")) {
                vocabularyService.importFromJson(file);
            } else if (fileName.endsWith(".txt")) {
                vocabularyService.importFromCsv(file);
            } else if (fileName.endsWith(".xlsx")) {
                vocabularyService.importFromXlsx(file);
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("mes", "Định dạng không hỗ trợ. Chỉ hỗ trợ CSV, JSON, TXT, XLSX"));
            }

            return ResponseEntity.ok(Map.of("mes", "Import dữ liệu thành công"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mes", "Lỗi khi đọc file: " + e.getMessage()));
        }
    }

    @PutMapping("/update/{id}")
    public Object updateVocabulary(@PathVariable Long id, @RequestBody VocabularyRequest re) {
        return vocabularyService.updateVocabulary(id, re);
    }

    @GetMapping("/getAll")
    public List<Vocabulary> getAllVocabulary() {
        return vocabularyService.getAllVocabulary();
    }

    @GetMapping("/getById/{id}")
    public Vocabulary getVocabularyById(@PathVariable Long id) {
        return vocabularyService.getById(id);
    }

    @GetMapping("/getByTopic/{id}")
    public List<Vocabulary> getByTopic(@PathVariable Long id) {
        return vocabularyService.getByTopic(id);
    }

    @GetMapping("/getByWord")
    public Vocabulary getVocabularyByWord(@RequestParam String word) {
        return vocabularyService.getByWord(word);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> delteAllLevel() {
        return vocabularyService.deleteAll();
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        return vocabularyService.deleteById(id);
    }

}

