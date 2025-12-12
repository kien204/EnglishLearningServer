package com.example.english_learning.service;

import com.example.english_learning.dto.request.SkillRequest;
import com.example.english_learning.models.Skill;
import com.example.english_learning.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    // -------------------- CRUD --------------------
    public List<Skill> getAll() {
        return skillRepository.findAll();
    }

    public Skill getById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Kỹ năng."));
    }

    public ResponseEntity<?> createSkill(SkillRequest req) {
        Skill skill = new Skill();
        skill.setName(req.getName());
        skill.setDescription(req.getDescription());
        skill.setCode(req.getCode());
        skill.setVisibleOnTopbar(req.getVisibleOnTopbar());
        skillRepository.save(skill);
        return ResponseEntity.ok("Tạo Kỹ năng thành công.");
    }

    public ResponseEntity<?> updateSkill(Long id, SkillRequest req) {
        Skill skill = getById(id);
        skill.setName(req.getName());
        skill.setDescription(req.getDescription());
        skill.setCode(req.getCode());
        skill.setVisibleOnTopbar(req.getVisibleOnTopbar());
        skillRepository.save(skill);
        return ResponseEntity.ok("Cập nhật Kỹ năng thành công.");
    }

    public ResponseEntity<?> deleteById(Long id) {
        Skill skill = getById(id);
        skillRepository.delete(skill);
        return ResponseEntity.ok("Xóa Kỹ năng thành công.");
    }

    public ResponseEntity<?> deleteAll() {
        skillRepository.deleteAll();
        return ResponseEntity.ok("Xóa tất cả Kỹ năng thành công.");
    }
}
