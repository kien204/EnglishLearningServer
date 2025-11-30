package com.example.english_learning.service.quiz;

import com.example.english_learning.dto.request.quiz.QuestionRequest;
import com.example.english_learning.models.Exercise;
import com.example.english_learning.models.Question;
import com.example.english_learning.repository.quiz.ExerciseRepository;
import com.example.english_learning.repository.quiz.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    public ResponseEntity<?> getAllQuestions() {
        return ResponseEntity.ok(questionRepository.findAll());
    }

    public ResponseEntity<?> getQuestionById(Long id) {
        Question question = questionRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Câu hỏi không tồn tại"));
        return ResponseEntity.ok(question);
    }

    public ResponseEntity<?> createQuestion(QuestionRequest request) {
        Exercise exercise = exerciseRepository.findById(request.getExerciseId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Bài tập không tồn tại")
        );

        Question savedQuestion = Question.builder()
                .questionText(request.getQuestionText())
                .ordering(request.getOrdering())
                .exercise(exercise)
                .build();
        questionRepository.save(savedQuestion);
        return ResponseEntity.ok("Tạo câu hỏi thành công");
    }

    public ResponseEntity<?> updateQuestion(Long id, QuestionRequest request) {
        Exercise exercise = exerciseRepository.findById(request.getExerciseId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Bài tập không tồn tại")
        );

        Question existingQuestion = questionRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Câu hỏi không tồn tại"));

        existingQuestion.setQuestionText(request.getQuestionText());
        existingQuestion.setOrdering(request.getOrdering());
        existingQuestion.setExercise(exercise);
        questionRepository.save(existingQuestion);
        return ResponseEntity.ok("Cập nhật câu hỏi thành công");
    }

    public ResponseEntity<?> deleteAllQuestions() {
        questionRepository.deleteAll();
        return ResponseEntity.ok("Xóa tất cả câu hỏi thành công");
    }

    public ResponseEntity<?> deleteQuestionById(Long id) {
        Question existingQuestion = questionRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Câu hỏi không tồn tại"));
        questionRepository.delete(existingQuestion);
        return ResponseEntity.ok("Xóa câu hỏi thành công");
    }
}
