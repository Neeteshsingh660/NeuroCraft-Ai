package com.example.aistudio_backend.controller;

import com.example.aistudio_backend.dto.ImageRequest;
import com.example.aistudio_backend.service.ImageGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:5173")
public class ImageController {

    @Autowired
    private ImageGenerationService imageGenerationService;

    @PostMapping("/generate-image")
    public ResponseEntity<Map<String, String>> generateImage(@RequestBody ImageRequest request) {
        String resultUrl = imageGenerationService.generateAndSaveImage(request.getPrompt());

        if (resultUrl.startsWith("Error")) {
            // Return 400 Bad Request if generation failed
            return ResponseEntity.badRequest().body(Map.of("error", resultUrl));
        }

        // Return the permanent Cloudinary URL
        return ResponseEntity.ok(Map.of("imageUrl", resultUrl));
    }
}