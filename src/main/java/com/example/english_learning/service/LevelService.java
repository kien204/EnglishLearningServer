package com.example.english_learning.service;

import com.example.english_learning.dto.request.LevelResquest;
import com.example.english_learning.models.Level;
import com.example.english_learning.repository.LevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class LevelService {
    @Autowired
    private LevelRepository levelRepository;

    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(levelRepository.findAll());
    }

    public ResponseEntity<?> findById(Long id) {
        Level level = levelRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Không tìm thấy Cấp độ"));
        return ResponseEntity.ok(level);
    }

    public ResponseEntity<?> createLevel(LevelResquest resquest) {
        Level level = new Level();
        level.setName(resquest.getName());
        level.setCode(resquest.getCode());
        level.setOrdering(resquest.getOrdering());
        levelRepository.save(level);
        return ResponseEntity.ok(Map.of("message", "Thêm Cấp độ mới thành công"));
    }

    public ResponseEntity<?> updateLevel(Long id, LevelResquest resquest) {
        Level level = levelRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Không tìm thấy cấp độ cần cập nhật"));
        level.setName(resquest.getName());
        level.setCode(resquest.getCode());
        level.setOrdering(resquest.getOrdering());
        level.setId(id);
        levelRepository.save(level);

        return ResponseEntity.ok(Map.of("message", "Cập nhật Cấp độ thành công"));

    }

    public ResponseEntity<?> deleteLevel(Long id) {
        if (!levelRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Cấp độ cần xóa");
        }
        levelRepository.deleteById(id);

        return ResponseEntity.ok(Map.of("message", "Xóa Cấp độ thành công"));
    }

    public ResponseEntity<?> deleteAll() {

        levelRepository.deleteAll();

        return ResponseEntity.ok(Map.of("message", "Xóa tất cả Cấp độ thành công"));
    }
}
