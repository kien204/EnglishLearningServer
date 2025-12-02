package com.example.english_learning.service.quiz;

import com.example.english_learning.dto.request.quiz.GroupOptionRequest;
import com.example.english_learning.models.Exercise;
import com.example.english_learning.models.GroupOption;
import com.example.english_learning.repository.quiz.ExerciseRepository;
import com.example.english_learning.repository.quiz.GroupOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GroupOptionService {
    @Autowired
    private GroupOptionRepository groupOptionRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(groupOptionRepository.findAll());
    }

    public ResponseEntity<?> getOptionTextsByExerciseId(Long exerciseId) {
        if (!exerciseRepository.existsById(exerciseId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "exercise not found");
        }

        return ResponseEntity.ok(groupOptionRepository.findOptionTextByExerciseId(exerciseId));
    }

    public ResponseEntity<?> getById(Long id) {
        if (!groupOptionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "group option not found");
        }

        return ResponseEntity.ok(groupOptionRepository.findById(id));
    }

    public ResponseEntity<?> saveGroup(GroupOptionRequest request) {
        Exercise exercise = exerciseRepository.findById(request.getExerciseId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy Bài tập")
        );


        GroupOption groupOption = new GroupOption();
        groupOption.setOptionText(request.getOptionText());
        groupOption.setExercise(exercise);

        groupOptionRepository.save(groupOption);
        return ResponseEntity.ok("Tạo lựa chọn thành công");
    }

    public ResponseEntity<?> updateGroup(Long id, GroupOptionRequest request) {
        if (!groupOptionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "group option not found");
        }

        Exercise exercise = exerciseRepository.findById(request.getExerciseId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy Bài tập")
        );

        GroupOption groupOption = new GroupOption();
        groupOption.setId(id);
        groupOption.setOptionText(request.getOptionText());
        groupOption.setExercise(exercise);

        groupOptionRepository.save(groupOption);
        return ResponseEntity.ok("Cập nhật lựa chọn thành công");
    }

    public ResponseEntity<?> deleteAll() {
        groupOptionRepository.deleteAll();
        return ResponseEntity.ok("Xóa tất cả lựa chọn thành công");
    }

    public ResponseEntity<?> deleteGroup(Long id) {
        if (!groupOptionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "group option not found");
        }

        groupOptionRepository.deleteById(id);
        return ResponseEntity.ok("Xóa lựa chọn thành công");
    }
}
