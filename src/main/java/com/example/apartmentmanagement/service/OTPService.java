package com.example.apartmentmanagement.service;

public interface OTPService {
    String generateOtp(String emailOrPhone);
    boolean validateOtp(String emailOrPhone, String inputOtp);
}
