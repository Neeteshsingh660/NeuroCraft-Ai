package com.example.aistudio_backend.dto;


import lombok.Data;

@Data
public class ImageRequest {
    private String prompt;
    private String size; // Optional: e.g., "1024x1024"
}