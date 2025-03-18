package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.*;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.service.EmailService;
import com.example.apartmentmanagement.service.OTPService;
import com.example.apartmentmanagement.util.AESUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.apartmentmanagement.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class HomeController {

    @Autowired
    private OTPService otpService;
    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            String usernameOrEmail = loginRequestDTO.getUsernameOrEmail();
            String password = loginRequestDTO.getPassword();
            User user = userService.getUserByEmailOrUserName(usernameOrEmail);
            if (user == null || !password.equals(AESUtil.decrypt(user.getPassword()))) {
                response.put("status", HttpStatus.UNAUTHORIZED.value());
                response.put("message", "Đăng nhập thất bại");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            session.setAttribute("user", user);
            System.out.println("Session ID after login: " + session.getId());

            Map<String, Object> dto = new HashMap<>();
            dto.put("user", user.getUserName());
            dto.put("password", password);
            dto.put("fullName", user.getFullName());
            dto.put("email", user.getEmail());
            dto.put("phone", user.getPhone());
            dto.put("role", user.getRole());
            dto.put("description", user.getDescription());
            dto.put("userImgUrl", user.getUserImgUrl());
            dto.put("age", user.getAge());
            dto.put("birthday", user.getBirthday());
            dto.put("idNumber", user.getIdNumber());
            dto.put("job", user.getJob());
            dto.put("apartment",
                    (user.getApartment() != null) ? user.getApartment().getApartmentName() : "Chưa sinh sống tại apartment nào"
            );
            response.put("data", dto);
            response.put("message", "Đăng nhập thành công");
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "Lỗi hệ thống: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/getUser")
    public ResponseEntity<Object> getUser(@RequestParam (value = "id") Long id) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.getUserById(id);
        if (user != null) {
            Map<String, Object> dto = new HashMap<>();
            dto.put("user", user.getUserName());
            dto.put("password", AESUtil.decrypt(user.getPassword()));
            dto.put("fullName", user.getFullName());
            dto.put("email", user.getEmail());
            dto.put("phone", user.getPhone());
            dto.put("role", user.getRole());
            dto.put("description", user.getDescription());
            dto.put("userImgUrl", user.getUserImgUrl());
            dto.put("age", user.getAge());
            dto.put("birthday", user.getBirthday());
            dto.put("idNumber", user.getIdNumber());
            dto.put("job", user.getJob());
            dto.put("apartment", user.getApartment().getApartmentName());
            response.put("data", dto);
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } else {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "Không tìm thấy user này");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequestDTO request) {
        String otp = otpService.generateOtp(request.getEmail());
        Map<String, Object> response = new HashMap<>();
        try {
            VerifyRegisterRequestDTO verifyRegisterRequestDTO = userService.verifyRegister(request);
            emailService.sendOtpEmail(request.getEmail(), otp);
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Đã gửi otp");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Nhập đầy đủ các trường");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Object> verifyOtp(@RequestBody RegisterRequestDTO request) {
        String email = request.getEmail();
        String otp = request.getOtp();
        if (otpService.validateOtp(email, otp)) {
            Map<String, Object> response = new HashMap<>();
            try {
                RegisterResponseDTO registerResponseDTO = userService.register(request);
                response.put("status", HttpStatus.CREATED.value());
                response.put("message", "Đăng ký thành công");
                response.put("data", registerResponseDTO);
                return ResponseEntity.ok(response);
            } catch (RuntimeException e) {
                response.put("status", HttpStatus.BAD_REQUEST.value());
                response.put("message", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP");
    }

    @PostMapping("/log_out")
    public ResponseEntity<Object> logout(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("Session ID: " + session.getId());
        if (session.getAttribute("user") == null) {
            response.put("message", "Bạn chưa đăng nhập");
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        session.invalidate();
        response.put("message", "Đăng xuất thành công");
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

}
