package com.example.aistudio_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private EmailService emailService;

    private static final String OTP_PREFIX = "OTP:";
    private static final long OTP_EXPIRE_MINUTES = 5;

    /**
     * Generates a 6-digit OTP, stores it in Redis with 5-min TTL,
     * and sends the email asynchronously.
     */
    public String generateAndSendOtp(String email) {
        // 1. Generate 6-digit secure OTP
        SecureRandom random = new SecureRandom();
        String otp = String.format("%06d", random.nextInt(1000000));

        // 2. Save in Redis with 5 minutes expiration time
        String redisKey = OTP_PREFIX + email;
        redisTemplate.opsForValue().set(redisKey, otp, OTP_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 3. Dispatch email asynchronously in background
        emailService.sendOtpEmail(email, otp);

        return "OTP sent successfully to " + email;
    }

    /**
     * Verifies user OTP against Redis key.
     */
    public boolean verifyOtp(String email, String userOtp) throws Exception {
        String redisKey = OTP_PREFIX + email;

        // 1. Fetch OTP from Redis
        String storedOtp = redisTemplate.opsForValue().get(redisKey);

        // 2. If null, key expired or was never generated
        if (storedOtp == null) {
            throw new Exception("OTP has expired or was not requested. Please request a new code.");
        }

        // 3. Compare codes
        if (!storedOtp.equals(userOtp)) {
            throw new Exception("Invalid OTP code!");
        }

        // 4. Delete OTP from Redis immediately after successful verification
        redisTemplate.delete(redisKey);

        return true;
    }
}