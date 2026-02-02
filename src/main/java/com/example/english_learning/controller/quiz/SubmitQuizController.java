package com.example.english_learning.controller.quiz;

import com.example.english_learning.dto.request.TranslateRequest;
import com.example.english_learning.dto.request.quiz.SubmitQuizRequest;
import com.example.english_learning.dto.request.quiz.WritingRequest;
import com.example.english_learning.dto.response.SubmitQuizResponse;
import com.example.english_learning.repository.quiz.SubmitAndResultQuizService;
import com.example.english_learning.service.other.AIService;
import com.example.english_learning.service.speech.PythonService;
import com.example.english_learning.service.speech.SpeakingScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/submit-quiz")
public class SubmitQuizController {
    @Autowired
    SubmitAndResultQuizService submitAndResultQuizService;
    @Autowired
    private PythonService pythonService;
    @Autowired
    private AIService AIService;
    @Autowired
    private SpeakingScoreService speakingScoreService;

    @PostMapping("/submit-quiz")
    public List<SubmitQuizResponse> submitQuiz(@RequestBody List<SubmitQuizRequest> request) {
        return submitAndResultQuizService.submitQuiz(request);
    }

    @PostMapping("/translate")
    public ResponseEntity<?> translate(@RequestBody TranslateRequest payload) {
        Map<String, Object> result = pythonService.translateText(payload);
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/submit-speaking/{exerciseId}", consumes = "multipart/form-data")
    public ResponseEntity<?> submitSpeaking(
            @PathVariable Long exerciseId,
            @RequestParam("audio") MultipartFile audio) {

        List<String> supportedTypes = List.of(
                "audio/mpeg",
                "audio/wav",
                "audio/x-wav",
                "audio/mp4"
        );

        String contentType = audio.getContentType();
        if (contentType == null || !supportedTypes.contains(contentType)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Chỉ chấp nhận file audio hợp lệ (MP3, WAV, MP4)"
            );
        }

        try {
            Map<String, Object> pythonResult = pythonService.scoreAudio(audio);
            Map<String, Object> finalScore = speakingScoreService.evaluate(exerciseId, pythonResult);
            return ResponseEntity.ok(finalScore);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống");
        }
    }


    @PostMapping(value = "/submit-writing")
    public Map<String, Object> submitWriting(@RequestBody WritingRequest request) {

        return AIService.gradeContent(request.getExerciseId(), request.getTranscript());
    }

    // Hàm phụ trợ để ép kiểu số an toàn (Tránh lỗi Integer vs Double)
    private double convertToDouble(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else if (value instanceof Double) {
            return (Double) value;
        }
        return 0.0;
    }
}
