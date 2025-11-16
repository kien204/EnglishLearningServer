package com.example.english_learning.service.grammar;

import com.example.english_learning.dto.request.grammar.GrammarGroupRequest;
import com.example.english_learning.models.GrammarGroup;
import com.example.english_learning.repository.grammar.GrammarGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class GrammarGroupService {
    @Autowired
    private GrammarGroupRepository repository;

    public Object createGrammarGroup(GrammarGroupRequest request) {
        GrammarGroup group = new GrammarGroup();
        group.setTitle(request.getTitle());
        repository.save(group);
        return Map.of("messenger", "Thêm thành công");
    }

    public ResponseEntity<?> updateGrammarGroup(Long id, GrammarGroupRequest entity) {

        GrammarGroup group = repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy mục cần sửa.")
                );

        group.setTitle(entity.getTitle());

        repository.save(group);

        return ResponseEntity.ok(Map.of("message", "Sửa thành công"));
    }

    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    public GrammarGroup getGrammarGroupById(Long id) {
        return repository.findById(id).orElseThrow(() -> (
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Mục không tồn tại")
        ));
    }

    public ResponseEntity<?> deleteGrammarGroup(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy mục cần xóa.");
        }

        try {
            repository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Xóa thành công"));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi trong quá trình xóa.");
        }
    }

}
