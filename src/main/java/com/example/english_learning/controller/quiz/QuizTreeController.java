package com.example.english_learning.controller.quiz;

import com.example.english_learning.dto.request.quiz.QuizTreeRequest;
import com.example.english_learning.dto.response.QuizTreeResponse;
import com.example.english_learning.service.quiz.QuizTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequestMapping("/quiz-tree")
@RestController
public class QuizTreeController {
    @Autowired
    private QuizTreeService quizTreeService;

    @GetMapping("/getBySkillTopicLevel/{skillId}/{topicId}/{levelId}")
    public List<QuizTreeResponse> getQuiz(@PathVariable Long skillId,
                                          @PathVariable Long topicId,
                                          @PathVariable Long levelId) {
        return quizTreeService.getQuizTree(skillId, topicId, levelId);
    }

    @PostMapping("/createQuizTree")
    public ResponseEntity<?> createQuizTree(@RequestBody List<QuizTreeRequest> quizTreeRequests) {
        return quizTreeService.createQuizTree(quizTreeRequests);
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
//            if (fileName.endsWith(".csv")) {
//                quizTreeService.importFromCsv(file);
//            } else if (fileName.endsWith(".json")) {
//                return vocabularyService.importFromJson(file);
//            } else if (fileName.endsWith(".txt")) {
//                vocabularyService.importFromCsv(file);
//            } else if (fileName.endsWith(".xlsx")) {
//                vocabularyService.importFromXlsx(file);
//            } else {
//                return ResponseEntity.badRequest()
//                        .body(Map.of("mes", "Định dạng không hỗ trợ. Chỉ hỗ trợ CSV, TXT, XLSX"));
//            }

            quizTreeService.importFromJson(file);

            return ResponseEntity.ok(Map.of("mes", "Import dữ liệu thành công"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mes", "Lỗi khi đọc file: " + e.getMessage()));
        }
    }
}
