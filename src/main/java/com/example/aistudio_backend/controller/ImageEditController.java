package com.example.aistudio_backend.controller;

import com.example.aistudio_backend.service.BackgroundRemovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:5173")
public class ImageEditController {

    @Autowired
    private BackgroundRemovalService backgroundRemovalService;

    @PostMapping("/remove-background")
    public ResponseEntity<Map<String, String>> removeBackground(@RequestParam("file") MultipartFile file) {

        // Validation: Ensure the user actually uploaded an image
        if (file.isEmpty() || !file.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Please upload a valid image file (JPG/PNG)."
            ));
        }

        String resultUrl = backgroundRemovalService.removeBackgroundAndSave(file);

        if (resultUrl.startsWith("Error")) {
            return ResponseEntity.badRequest().body(Map.of("error", resultUrl));
        }

        // Return the Cloudinary URL of the transparent image
        return ResponseEntity.ok(Map.of("imageUrl", resultUrl));
    }
}