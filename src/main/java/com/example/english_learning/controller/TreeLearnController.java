package com.example.english_learning.controller;

import com.example.english_learning.service.TreeLearnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/tree-learn")
@RestController
public class TreeLearnController {
    @Autowired
    private TreeLearnService treeLearnService;

    @GetMapping("/get-tree-vocabulary")
    public ResponseEntity<?> getTreeVocabulary() {
        return treeLearnService.getTreeVocabulary();
    }

    @GetMapping("/get-tree-grammar")
    public ResponseEntity<?> getTreeGrammar() {
        return treeLearnService.getTreeGrammar();
    }
}
