package com.example.english_learning.controller.quiz;

import com.example.english_learning.dto.request.quiz.QuizTreeRequest;
import com.example.english_learning.dto.response.QuizTreeResponse;
import com.example.english_learning.service.quiz.QuizTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/quiz-tree")
@RestController
public class QuizTreeController {
    @Autowired
    private QuizTreeService quizTreeService;

    @GetMapping("/getByTopic/{topicId}")
    public List<QuizTreeResponse> getQuiz(@PathVariable Long topicId) {
        return quizTreeService.getQuizTree(topicId);
    }

    @GetMapping("/getByGroupWord/{groupWord}")
    public List<QuizTreeResponse> getQuizByGroupWord(@PathVariable int groupWord) {
        return quizTreeService.getQuizByGroupWord(groupWord);
    }

    @PostMapping(
            value = "/upload-quiz-json",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<?> uploadQuizJson(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("mes", "File trống"));
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return ResponseEntity.badRequest().body(Map.of("mes", "Không xác định được tên file"));
        }
        try {
            if (fileName.endsWith(".json")) {
                quizTreeService.uploadQuizFile(file);
            } else if (fileName.endsWith(".xlsx")) {
                quizTreeService.importQuizTreeFromXLSX(file);
            } else {
                return ResponseEntity.badRequest().body("Chỉ chấp nhận file JSON hoặc XLSX");
            }

            return ResponseEntity.ok("Tạo cây câu hỏi từ file JSON thành công");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi đọc file JSON: " + e.getMessage());
        }
    }

    @GetMapping("/export/xlsx/{id}")
    public ResponseEntity<InputStreamResource> exportXlsx(@PathVariable Long id) throws IOException {

        ByteArrayInputStream stream = quizTreeService.exportQuizTreeXLSX(id);

        HttpHeaders headers = new HttpHeaders();
        headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=quiz.xlsx"
        );

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(new InputStreamResource(stream));
    }

    @PostMapping("/createQuizTree")
    public ResponseEntity<?> createQuizTree(@RequestBody QuizTreeRequest quizTreeRequests) {
        return quizTreeService.createQuizTree(quizTreeRequests);
    }

}
