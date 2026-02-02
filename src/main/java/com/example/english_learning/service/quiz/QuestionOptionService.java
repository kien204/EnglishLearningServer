package com.example.english_learning.service.quiz;

import com.example.english_learning.dto.request.quiz.QuestionOptionRequest;
import com.example.english_learning.models.Question;
import com.example.english_learning.models.QuestionOption;
import com.example.english_learning.repository.quiz.QuestionOptionRepository;
import com.example.english_learning.repository.quiz.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class QuestionOptionService {
    @Autowired
    private QuestionOptionRepository questionOptionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public ResponseEntity<?> create(QuestionOptionRequest request) {
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Câu hỏi"));

        QuestionOption questionOption = QuestionOption.builder()
                .question(question)
                .optionText(request.getOptionText())
                .isCorrect(request.getIsCorrect())
                .build();

        questionOptionRepository.save(questionOption);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Tạo tùy chọn câu hỏi thành công", "data", questionOption));
    }

    public ResponseEntity<?> update(Long id, QuestionOptionRequest request) {
        if (!questionOptionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Tùy chọn câu hỏi");
        }

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Câu hỏi"));


        QuestionOption questionOption = QuestionOption.builder()
                .question(question)
                .optionText(request.getOptionText())
                .isCorrect(request.getIsCorrect())
                .id(id)
                .build();

        questionOptionRepository.save(questionOption);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Cập nhật tùy chọn câu hỏi thành công", "data", questionOption));
    }
//
//    public ResponseEntity<?> getAll() {
//        return ResponseEntity.ok(questionOptionRepository.findAll());
//    }

    public ResponseEntity<?> findByQuestionId(Long id) {
        List<QuestionOption> questionOption = questionOptionRepository.findByQuestion_Id(id);

        return ResponseEntity.ok(questionOption);
    }

    public ResponseEntity<?> findById(Long id) {
        QuestionOption questionOption = questionOptionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Tùy chọn câu hỏi"));

        return ResponseEntity.ok(questionOption);
    }

    public ResponseEntity<?> deleteById(Long id) {
        QuestionOption questionOption = questionOptionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Tùy chọn câu hỏi"));

        questionOptionRepository.delete(questionOption);
        return ResponseEntity.ok(Map.of("message", "Xóa tùy chọn câu hỏi thành công"));
    }

    public ResponseEntity<?> deleteAll() {
        questionOptionRepository.deleteAll();
        return ResponseEntity.ok(Map.of("message", "Xóa tùy chọn câu hỏi thành công"));
    }
}
