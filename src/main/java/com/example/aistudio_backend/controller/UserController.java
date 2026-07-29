package com.example.aistudio_backend.controller;

import com.example.aistudio_backend.dto.RegisterRequest;
import com.example.aistudio_backend.entity.User;
import com.example.aistudio_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }

            User registeredUser = userService.registerUser(request.getEmail(), request.getPassword());

            return ResponseEntity.ok(Map.of(
                    "message", "User registered successfully on Free Tier!",
                    "userId", registeredUser.getId(),
                    "email", registeredUser.getEmail(),
                    "credits", registeredUser.getCredits(),
                    "currentPlan", registeredUser.getCurrentPlan()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Now accepts an optional userId query param (e.g. /profile?userId=2)
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestParam(required = false, defaultValue = "1") Long userId) {
        User user = userService.getUserDetails(userId);

        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "credits", user.getCredits(),
                "currentPlan", user.getCurrentPlan(),
                "planExpiryDate", user.getPlanExpiryDate() != null ? user.getPlanExpiryDate().toString() : "N/A"
        ));
    }
}