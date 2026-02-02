package com.example.english_learning.service.quiz;

import com.example.english_learning.dto.request.quiz.QuestionRequest;
import com.example.english_learning.models.Exercise;
import com.example.english_learning.models.Question;
import com.example.english_learning.models.Vocabulary;
import com.example.english_learning.repository.VocabularyRepository;
import com.example.english_learning.repository.quiz.ExerciseRepository;
import com.example.english_learning.repository.quiz.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private VocabularyRepository vocabularyRepository;

    public ResponseEntity<?> getAllQuestions() {
        return ResponseEntity.ok(questionRepository.findAll());
    }

    public ResponseEntity<?> getQuestionById(Long id) {
        Question question = questionRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Câu hỏi không tồn tại"));
        return ResponseEntity.ok(question);
    }

    public ResponseEntity<?> getQuestionsByQuizId(Long id) {
        List<Question> question = questionRepository.findByExercise_Id(id);
        return ResponseEntity.ok(question);
    }

    public ResponseEntity<?> createQuestion(QuestionRequest request) {
        Exercise exercise = exerciseRepository.findById(request.getExerciseId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Bài tập không tồn tại")
        );

        Vocabulary vocabulary = null;

        if (request.getVocabularyId() != null) {

            vocabulary = vocabularyRepository.findById(request.getVocabularyId()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Từ vựng không tồn tại"));
        }
        Question savedQuestion = Question.builder()
                .questionText(request.getQuestionText())
                .vocabulary(vocabulary)
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

        Vocabulary vocabulary = null;

        if (request.getVocabularyId() != null) {

            vocabulary = vocabularyRepository.findById(request.getVocabularyId()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Từ vựng không tồn tại"));
        }

        Question existingQuestion = questionRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Câu hỏi không tồn tại"));

        existingQuestion.setQuestionText(request.getQuestionText());
        existingQuestion.setVocabulary(vocabulary);
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
