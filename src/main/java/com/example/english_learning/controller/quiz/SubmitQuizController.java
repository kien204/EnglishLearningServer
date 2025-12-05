package com.example.english_learning.controller.quiz;

import com.example.english_learning.dto.request.quiz.SubmitQuizRequest;
import com.example.english_learning.dto.response.SubmitQuizResponse;
import com.example.english_learning.repository.quiz.SubmitAndResultQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/submit-quiz")
public class SubmitQuizController {
    @Autowired
    SubmitAndResultQuizService submitAndResultQuizService;

    @PostMapping("/submit-quiz")
    public List<SubmitQuizResponse> submitQuiz(@RequestBody List<SubmitQuizRequest> request) {
        return submitAndResultQuizService.submitQuiz(request);
    }
}
