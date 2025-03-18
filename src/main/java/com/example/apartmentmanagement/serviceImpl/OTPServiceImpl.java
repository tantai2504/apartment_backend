package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.service.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OTPServiceImpl implements OTPService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final long OTP_EXPIRE = 5;

    public String generateOtp(String emailOrPhone) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6 số
        redisTemplate.opsForValue().set(emailOrPhone, otp, OTP_EXPIRE, TimeUnit.MINUTES);
        return otp;
    }

    public boolean validateOtp(String emailOrPhone, String inputOtp) {
        String storedOtp = redisTemplate.opsForValue().get(emailOrPhone);
        return storedOtp != null && storedOtp.equals(inputOtp);
    }
}
