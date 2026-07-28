package com.example.aistudio_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ImageGenerationService {

    // Reuse the exact same Gemini key you used for text!
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Autowired
    private CloudinaryService cloudinaryService;

    // Google's official endpoint for Imagen 3
    private final String IMAGEN_URL = "https://generativelanguage.googleapis.com/v1beta/models/imagen-3.0-generate-001:predict";
    private final RestTemplate restTemplate = new RestTemplate();

    public String generateAndSaveImage(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", geminiApiKey); // Google's header requirement

        // Build request body for Google Imagen
        Map<String, Object> requestBody = new HashMap<>();

        // Imagen uses "instances" for the prompt
        requestBody.put("instances", List.of(
                Map.of("prompt", prompt)
        ));

        // Imagen uses "parameters" for settings like amount of images
        requestBody.put("parameters", Map.of(
                "sampleCount", 1
        ));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // 1. Hit the Gemini Imagen API
            ResponseEntity<Map> response = restTemplate.postForEntity(IMAGEN_URL, entity, Map.class);
            Map<?, ?> body = response.getBody();

            if (body != null && body.containsKey("predictions")) {
                List<?> predictions = (List<?>) body.get("predictions");
                if (!predictions.isEmpty()) {

                    // 2. Extract the Base64 image string
                    Map<?, ?> firstPrediction = (Map<?, ?>) predictions.get(0);
                    String base64Image = (String) firstPrediction.get("bytesBase64Encoded");

                    // 3. Decode Base64 to raw bytes
                    byte[] imageBytes = Base64.getDecoder().decode(base64Image);

                    // 4. Upload to Cloudinary and return the permanent URL
                    return cloudinaryService.uploadImageBytes(imageBytes);
                }
            }
            return "Error: Empty response received from Google Imagen API.";
        } catch (Exception e) {
            return "Error during Image Generation: " + e.getMessage();
        }
    }
}