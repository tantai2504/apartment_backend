package com.example.apartmentmanagement.service;

public interface EmailService {
    void sendOtpEmail(String to, String otp);

    void sendVerificationEmail(String to, String userName);
}
