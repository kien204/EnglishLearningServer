package com.example.english_learning.controller.quiz;


import com.example.english_learning.dto.request.quiz.QuestionOptionRequest;
import com.example.english_learning.service.quiz.QuestionOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question-options")
@RequiredArgsConstructor
public class QuestionOptionController {

    @Autowired
    private QuestionOptionService optionService;

//    @GetMapping("getAll")
//    public ResponseEntity<?> getAllQuestionOptions() {
//        return optionService.getAll();
//    }

    @GetMapping("getById/{id}")
    public ResponseEntity<?> getQuestionOptionById(@PathVariable Long id) {
        return optionService.findById(id);
    }

    @GetMapping("getByQuestionId/{questionId}")
    public ResponseEntity<?> getOptionsByQuestionId(@PathVariable Long questionId) {
        return optionService.findByQuestionId(questionId);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createQuestionOption(@RequestBody QuestionOptionRequest questionOptionRequest) {
        return optionService.create(questionOptionRequest);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateQuestionOption(@PathVariable Long id, @RequestBody QuestionOptionRequest questionOptionRequest) {
        return optionService.update(id, questionOptionRequest);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> deleteAllQuestionOptions() {
        return optionService.deleteAll();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteQuestionOption(@PathVariable Long id) {
        return optionService.deleteById(id);
    }
}
