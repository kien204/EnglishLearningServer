package com.example.english_learning.controller.grammar;

import com.example.english_learning.dto.request.grammar.GrammarGroupRequest;
import com.example.english_learning.models.GrammarGroup;
import com.example.english_learning.service.grammar.GrammarGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/grammarGroup")
public class GrammarGroupController {
    @Autowired
    private GrammarGroupService grammarGroupService;

    // CREATE
    @PostMapping("/create")
    public Object create(@RequestBody GrammarGroupRequest request) {
        return grammarGroupService.createGrammarGroup(request);
    }

    // UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody GrammarGroupRequest request
    ) {
        return grammarGroupService.updateGrammarGroup(id, request);
    }

    // GET All
    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        return grammarGroupService.getAll();
    }

    // GET BY ID
    @GetMapping("/getById/{id}")
    public GrammarGroup getById(@PathVariable Long id) {
        return grammarGroupService.getGrammarGroupById(id);
    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return grammarGroupService.deleteGrammarGroup(id);
    }
}
