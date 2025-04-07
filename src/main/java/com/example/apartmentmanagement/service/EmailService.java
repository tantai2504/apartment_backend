package com.example.apartmentmanagement.service;

public interface EmailService {
    void sendVerificationEmail(String to, String userName);
    void sendRegistrationOtpEmail(String to, String otp);
    public void sendForgotPasswordOtpEmail(String to, String otp);
    void sendEmail(String to, String subject, String text);
}
