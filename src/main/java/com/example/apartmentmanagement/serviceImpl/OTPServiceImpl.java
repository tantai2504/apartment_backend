package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.service.OTPService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class OTPServiceImpl implements OTPService {

    private static final long OTP_EXPIRE = 5;
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();

    public OTPServiceImpl() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            otpStore.clear();
        }, OTP_EXPIRE, OTP_EXPIRE, TimeUnit.MINUTES);
    }

    public String generateOtp(String emailOrPhone) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        otpStore.put(emailOrPhone, otp);
        return otp;
    }

    public boolean validateOtp(String emailOrPhone, String inputOtp) {
        String storedOtp = otpStore.get(emailOrPhone);
        return storedOtp != null && storedOtp.equals(inputOtp);
    }
}
