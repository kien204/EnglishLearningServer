package com.example.english_learning.service.grammar;

import com.example.english_learning.dto.request.grammar.GrammarCategoryRequest;
import com.example.english_learning.models.GrammarCategory;
import com.example.english_learning.models.GrammarGroup;
import com.example.english_learning.repository.grammar.GrammarCategoryRepository;
import com.example.english_learning.repository.grammar.GrammarGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class GrammarCategoryService {
    @Autowired
    private GrammarCategoryRepository grammarCategoryRepository;

    @Autowired
    private GrammarGroupService grammarCategoryService;

    @Autowired
    private GrammarGroupRepository grammarGroupRepository;

    public ResponseEntity<?> create(GrammarCategoryRequest request) {
        GrammarGroup grammarGroup = grammarCategoryService.getGrammarGroupById(request.getGroupId());

        GrammarCategory grammarCategory = new GrammarCategory();
        grammarCategory.setTitle(request.getTitle());
        grammarCategory.setGroup(grammarGroup);
        grammarCategoryRepository.save(grammarCategory);
        return ResponseEntity.ok("Tạo thành công.");
    }

    public ResponseEntity<?> createByJson(List<GrammarCategoryRequest> request) {
        for (GrammarCategoryRequest request1 : request) {
            GrammarGroup grammarGroup = grammarCategoryService.getGrammarGroupById(request1.getGroupId());

            GrammarCategory grammarCategory = new GrammarCategory();
            grammarCategory.setTitle(request1.getTitle());
            grammarCategory.setGroup(grammarGroup);
            grammarCategoryRepository.save(grammarCategory);
        }
        return ResponseEntity.ok("Tạo thành công.");
    }

    public ResponseEntity<?> update(Long id, GrammarCategoryRequest request) {
        GrammarCategory grammarCategory = grammarCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy nhóm ngữ pháp id = " + id
                ));

        GrammarGroup grammarGroup = grammarCategoryService.getGrammarGroupById(request.getGroupId());

        grammarCategory.setTitle(request.getTitle());
        grammarCategory.setGroup(grammarGroup);
        grammarCategoryRepository.save(grammarCategory);

        return ResponseEntity.ok("Sửa thành công");
    }

    public ResponseEntity<List<GrammarCategory>> findAll() {
        return ResponseEntity.ok(grammarCategoryRepository.findAll());
    }

    public GrammarCategory findById(Long id) {
        return grammarCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy nhóm ngữ pháp"
                ));
    }
}
