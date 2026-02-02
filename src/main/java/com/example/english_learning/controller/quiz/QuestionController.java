package com.example.english_learning.controller.quiz;

import com.example.english_learning.dto.request.quiz.QuestionRequest;
import com.example.english_learning.service.quiz.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id);
    }

    @GetMapping("/getByQuizId/{quizId}")
    public ResponseEntity<?> getQuestionsByQuizId(@PathVariable Long quizId) {
        return questionService.getQuestionsByQuizId(quizId);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createQuestion(@RequestBody QuestionRequest request) {
        return questionService.createQuestion(request);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long id, @RequestBody QuestionRequest request) {
        return questionService.updateQuestion(id, request);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> deleteAllQuestions() {
        return questionService.deleteAllQuestions();
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> deleteQuestionById(@PathVariable Long id) {
        return questionService.deleteQuestionById(id);
    }
}
