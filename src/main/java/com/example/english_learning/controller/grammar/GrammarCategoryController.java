package com.example.english_learning.controller.grammar;

import com.example.english_learning.dto.request.grammar.GrammarCategoryRequest;
import com.example.english_learning.models.GrammarCategory;
import com.example.english_learning.service.grammar.GrammarCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/grammarCategory")
public class GrammarCategoryController {

    @Autowired
    private GrammarCategoryService grammarCategoryService;

    // CREATE
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody GrammarCategoryRequest request) {
        return grammarCategoryService.create(request);
    }

    @PostMapping("/createByJson")
    public ResponseEntity<?> createJson(@RequestBody List<GrammarCategoryRequest> request) {
        return grammarCategoryService.createByJson(request);
    }

    // UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody GrammarCategoryRequest request
    ) {
        return grammarCategoryService.update(id, request);
    }

    // GET ALL
    @GetMapping("getAll")
    public ResponseEntity<List<GrammarCategory>> findAll() {
        return grammarCategoryService.findAll();
    }
}
