package com.example.english_learning.controller;

import com.example.english_learning.dto.request.UserRequest;
import com.example.english_learning.models.User;
import com.example.english_learning.service.AuthService;
import com.example.english_learning.service.other.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/user-profile")
@RestController
public class UserProfileComtroller {

    @Autowired
    private AuthService authService;
    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("/get-profile/{id}")
    public User getUserProfile(@PathVariable Long id) {
        return authService.getUserById(id);
    }

    @PatchMapping(
            value = "/{id}/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public String updateAvatar(
            @PathVariable Long id,
            @RequestParam MultipartFile avatar
    ) {
        String avatarUrl = cloudinaryService.uploadFile(avatar);
        return authService.updateAvatar(id, avatarUrl);
    }

    @PutMapping("/update-profile/{id}")
    public String updateUserProfile(
            @PathVariable Long id,
            @RequestBody UserRequest request
    ) {
        return authService.updateProfile(id, request);
    }


    @PutMapping("/change-password/{id}")
    public String changePassword(@PathVariable Long id,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword) {
        return authService.changePassword(id, oldPassword, newPassword);
    }
}
