package com.example.aistudio_backend.service;

import com.example.aistudio_backend.entity.Transaction;
import com.example.aistudio_backend.entity.User;
import com.example.aistudio_backend.repository.TransactionRepository;
import com.example.aistudio_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MockPaymentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction processMockPayment(String planType, Long userId) {

        // 1. Fetch the user from the database (or create a dummy one for testing)
        User user = userRepository.findById(userId).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail("testuser@college.edu");
            newUser.setCredits(0);
            newUser.setCurrentPlan("FREE");
            return userRepository.save(newUser);
        });

        // 2. Determine credits and expiry based on the plan
        int creditsToAdd = 0;
        int price = 0;
        LocalDateTime expiryDate = LocalDateTime.now();

        switch (planType.toUpperCase()) {
            case "WEEKLY":
                creditsToAdd = 50;
                price = 199; // ₹199
                expiryDate = expiryDate.plusWeeks(1);
                break;
            case "MONTHLY":
                creditsToAdd = 300;
                price = 499; // ₹499
                expiryDate = expiryDate.plusMonths(1);
                break;
            case "YEARLY":
                creditsToAdd = 5000;
                price = 3999; // ₹3999
                expiryDate = expiryDate.plusYears(1);
                break;
            default:
                throw new IllegalArgumentException("Invalid plan selected");
        }

        // 3. Update User Data and Save to Database
        user.setCredits(user.getCredits() + creditsToAdd);
        user.setCurrentPlan(planType.toUpperCase());
        user.setPlanExpiryDate(expiryDate);
        userRepository.save(user);

        // 4. Create a Fake Transaction Record and Save to Database
        Transaction transaction = new Transaction();
        transaction.setUserId(user.getId());
        transaction.setPlanPurchased(planType.toUpperCase());
        transaction.setAmountPaid(price);
        transaction.setPaymentDate(LocalDateTime.now());
        // Generate a random ID that looks like a real bank transaction (e.g. TXN-a1b2c3d4...)
        transaction.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());

        return transactionRepository.save(transaction);
    }
}