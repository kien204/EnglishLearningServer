package com.example.english_learning.controller;

import com.example.english_learning.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin/dashboard")
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        return dashboardService.getDashboardStats();
    }

    @GetMapping("/user-stats")
    public ResponseEntity<?> getUserStats() {
        return dashboardService.getUserStats();
    }
}
