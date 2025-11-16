package com.example.english_learning.controller.grammar;

import com.example.english_learning.dto.response.GroupTreeResponse;
import com.example.english_learning.service.grammar.GrammarTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/grammar")
public class GrammarTreeController {

    @Autowired
    private GrammarTreeService grammarTreeService;

    @GetMapping("/getTree")
    public List<GroupTreeResponse> getTree() {
        return grammarTreeService.buildTree();
    }
}

