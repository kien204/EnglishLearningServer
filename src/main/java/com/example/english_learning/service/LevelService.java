package com.example.english_learning.service;

import com.example.english_learning.dto.request.LevelResquest;
import com.example.english_learning.models.Level;
import com.example.english_learning.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LevelService {

    private final LevelRepository levelRepository;

    // -------------------- CRUD --------------------
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(levelRepository.findAll());
    }

    public Level getById(Long id) {
        return levelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Cấp độ id = " + id));
    }

    public ResponseEntity<?> createLevel(LevelResquest req) {
        Level level = new Level();
        level.setName(req.getName());
        level.setCode(req.getCode());
        level.setOrdering(req.getOrdering());
        levelRepository.save(level);
        return ResponseEntity.ok("Tạo cấp độ thành công");
    }

    public ResponseEntity<?> updateLevel(Long id, LevelResquest req) {
        Level level = getById(id);
        level.setName(req.getName());
        level.setCode(req.getCode());
        level.setOrdering(req.getOrdering());
        levelRepository.save(level);
        return ResponseEntity.ok("Cập nhật cấp độ thành công");
    }

    public ResponseEntity<?> deleteById(Long id) {
        Level level = getById(id);
        levelRepository.delete(level);
        return ResponseEntity.ok("Xóa cấp độ thành công");
    }

    public ResponseEntity<?> deleteAll() {
        levelRepository.deleteAll();
        return ResponseEntity.ok("Xóa tất cả cấp độ thành công");
    }
}
