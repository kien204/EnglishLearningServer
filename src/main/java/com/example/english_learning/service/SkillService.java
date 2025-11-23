package com.example.english_learning.service;

import com.example.english_learning.dto.request.SkillRequest;
import com.example.english_learning.models.Skill;
import com.example.english_learning.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class SkillService {
    @Autowired
    private SkillRepository skillRepository;

    public ResponseEntity<?> getAll() {
        List<Skill> skills = skillRepository.findAll();

        return ResponseEntity.ok(skills);
    }

    public ResponseEntity<?> getById(Long id) {
        Skill skill = skillRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Không tìm thấy Kỹ năng"));
        return ResponseEntity.ok(skill);
    }

    public ResponseEntity<?> createSkill(SkillRequest request) {

        Skill skill = new Skill();
        skill.setName(request.getName());
        skill.setDescription(request.getDescription());
        skill.setCode(request.getCode());
        skillRepository.save(skill);

        return ResponseEntity.ok(Map.of("message", "Thêm Kỹ năng mới thành công"));
    }

    public ResponseEntity<?> updateSkill(Long id, SkillRequest request) {
        Skill skill = skillRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Kỹ năng id = " + id));

        skill.setName(request.getName());
        skill.setDescription(request.getDescription());
        skill.setCode(request.getCode());
        skill.setId(id);

        skillRepository.save(skill);

        return ResponseEntity.ok(Map.of("message", "Cập nhật Kỹ năng thành công"));
    }

    public ResponseEntity<?> deleteSkill(Long id) {

        if (!skillRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Kỹ năng id = " + id);
        }

        skillRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Xóa Kỹ năng thành công"));
    }

    public ResponseEntity<?> deleteAll() {
        skillRepository.deleteAll();
        return ResponseEntity.ok(Map.of("message", "Xóa Kỹ năng tất cả thành công"));
    }
}
