package com.example.aistudio_backend.controller;

import com.example.aistudio_backend.dto.MockPaymentRequest;
import com.example.aistudio_backend.entity.Transaction;
import com.example.aistudio_backend.service.MockPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

    @Autowired
    private MockPaymentService mockPaymentService;

    @PostMapping("/mock-checkout")
    public ResponseEntity<?> mockCheckout(@RequestBody MockPaymentRequest request) {
        try {
            // Hardcoding User ID 1 for now until we integrate JWT Auth later
            Long currentUserId = 1L;

            Transaction successfulTxn = mockPaymentService.processMockPayment(request.getPlan(), currentUserId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Payment Successful!",
                    "transactionId", successfulTxn.getTransactionId(),
                    "plan", successfulTxn.getPlanPurchased()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}