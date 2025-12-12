package com.example.english_learning.controller;

import com.example.english_learning.dto.response.TopbarResponse;
import com.example.english_learning.service.TopbarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/topbar-controller")
public class TopbarController {
    @Autowired
    private TopbarService topbarService;

    @GetMapping("/get-topbar")
    public List<TopbarResponse> getTopbar() {
        return topbarService.getMenuTopbar();
    }
}
