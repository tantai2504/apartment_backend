package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.ForgotPasswordDTO;
import com.example.apartmentmanagement.dto.LoginRequestDTO;
import com.example.apartmentmanagement.dto.ResetPasswordDTO;
import com.example.apartmentmanagement.dto.RegisterRequestDTO;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.serviceImpl.EmailService_Han;
import com.example.apartmentmanagement.util.AESUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.apartmentmanagement.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService_Han emailService;
    private final Map<String, String> otpStorage = new HashMap<>();
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
            dto.put("userId", user.getUserId());
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
            dto.put("apartment", user.getApartment().getApartmentName());
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
    public ResponseEntity<Object> register(@RequestBody RegisterRequestDTO registerRequestDTO) {

        return null;
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

    @PostMapping("/forgot_password")
    public ResponseEntity<Object> forgotPassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        Map<String, Object> response = new HashMap<>();
        String email = forgotPasswordDTO.getEmail();
        User user = userService.getUserByEmailOrUserName(email);

        if (user == null) {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "Email không tồn tại trong hệ thống");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // Tạo mã OTP 6 chữ số
        otpStorage.put(email, otp);
        emailService.sendOtpEmail(email, otp);

        response.put("message", "Mã OTP đã được gửi đến email của bạn");
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset_password")
    public ResponseEntity<Object> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        Map<String, Object> response = new HashMap<>();
        String email = resetPasswordDTO.getEmail();
        String otp = resetPasswordDTO.getOtp();
        String newPassword = resetPasswordDTO.getNewPassword();

        if (!otpStorage.containsKey(email) || !otpStorage.get(email).equals(otp)) {
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            response.put("message", "Mã OTP không hợp lệ hoặc đã hết hạn");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        User user = userService.getUserByEmailOrUserName(email);
        if (user == null) {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "Người dùng không tồn tại");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        user.setPassword(AESUtil.encrypt(newPassword));
        userService.saveUser(user);
        otpStorage.remove(email);

        response.put("message", "Mật khẩu đã được thay đổi thành công");
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

}
