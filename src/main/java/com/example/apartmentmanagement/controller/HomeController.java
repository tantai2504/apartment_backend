package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.*;
import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.service.EmailService;
import com.example.apartmentmanagement.service.OTPService;
import com.example.apartmentmanagement.dto.ForgotPasswordDTO;
import com.example.apartmentmanagement.dto.LoginRequestDTO;
import com.example.apartmentmanagement.dto.ResetPasswordDTO;
import com.example.apartmentmanagement.dto.RegisterRequestDTO;
import com.example.apartmentmanagement.util.AESUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.apartmentmanagement.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

    private final Map<String, String> otpStorage = new HashMap<>();
    private Map<String, Long> otpVerifiedTime = new HashMap<>(); // email -> timestamp (ms)
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            String usernameOrEmail = loginRequestDTO.getUsernameOrEmail();
            String password = loginRequestDTO.getPassword();
            User user = userService.getUserByEmailOrUserName(usernameOrEmail);
            if (user == null || !password.equals(AESUtil.decrypt(user.getPassword()))) {
                response.put("status", HttpStatus.UNAUTHORIZED.value());
                response.put("message", "Đăng nhập thất bại, sai tên tài khoản hoặc mật khẩu");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            session.setAttribute("user", user);
            System.out.println("Session ID after login: " + session.getId());

            Map<String, Object> dto = new HashMap<>();
            dto.put("userId", user.getUserId());
            dto.put("userName", user.getUserName());
            dto.put("password", user.getPassword());
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
            if(user.isRentor()){
                dto.put("isRentor",true);
            }else{
                dto.put("isRentor",false);
            }
            dto.put("accountBallance", user.getAccountBalance());
            dto.put("apartment",
                    (user.getApartments() != null && !user.getApartments().isEmpty())
                            ? user.getApartments().stream().map(Apartment::getApartmentName).toList()
                            : List.of("Chưa sinh sống tại apartment nào")
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
    public ResponseEntity<Object> getUser(@RequestParam(value = "id") Long id) {
        Map<String, Object> response = new HashMap<>();
        UserRequestDTO user = userService.getUserById(id);
        if (user != null) {
            response.put("data", user);
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
            emailService.sendRegistrationOtpEmail(request.getEmail(), otp);
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Đã gửi otp");
            response.put("data", verifyRegisterRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Object> verifyOtp(@RequestBody VerifyOTPRequestDTO request) {
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
        emailService.sendForgotPasswordOtpEmail(email, otp);

        response.put("message", "Mã OTP đã được gửi đến email của bạn");
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
    @PostMapping("/verify_otp")
    public ResponseEntity<Object> verifyOtp(@RequestBody VerifyOtpForgotPassDTO verifyOtpDTO) {
        Map<String, Object> response = new HashMap<>();
        String email = verifyOtpDTO.getEmail();
        String otp = verifyOtpDTO.getOtp();

        if (!otpStorage.containsKey(email) || !otpStorage.get(email).equals(otp)) {
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            response.put("message", "Mã OTP không hợp lệ hoặc đã hết hạn");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        otpVerifiedTime.put(email, System.currentTimeMillis()); // Ghi nhận thời gian xác thực

        response.put("message", "Xác thực OTP thành công. Vui lòng đặt lại mật khẩu trong 5 phút.");
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
    @PostMapping("/reset_password")
    public ResponseEntity<Object> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        Map<String, Object> response = new HashMap<>();
        String email = resetPasswordDTO.getEmail();
        String newPassword = resetPasswordDTO.getNewPassword();

        if (!otpVerifiedTime.containsKey(email)) {
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            response.put("message", "Bạn chưa xác thực OTP hoặc OTP đã hết hạn");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        long verifiedTime = otpVerifiedTime.get(email);
        long now = System.currentTimeMillis();
        long fiveMinutes = 5 * 60 * 1000;

        if (now - verifiedTime > fiveMinutes) {
            // Hết hạn rồi
            otpVerifiedTime.remove(email);
            otpStorage.remove(email);
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            response.put("message", "Phiên xác thực OTP đã hết hạn. Vui lòng xác thực lại.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        User user = userService.getUserByEmailOrUserName(email);
        if (user == null) {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "Người dùng không tồn tại");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        String decryptedOldPassword = AESUtil.decrypt(user.getPassword());
        if (decryptedOldPassword.equals(newPassword)) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Mật khẩu mới không được giống với mật khẩu cũ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        user.setPassword(AESUtil.encrypt(newPassword));
        userService.saveUser(user);

        // Xoá OTP và thời gian xác thực
        otpStorage.remove(email);
        otpVerifiedTime.remove(email);

        response.put("message", "Mật khẩu đã được đặt lại thành công");
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change_password")
    public ResponseEntity<Object> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        Map<String, Object> response = new HashMap<>();

        String email = changePasswordDTO.getEmail();
        String currentPassword = changePasswordDTO.getCurrentPassword();
        String newPassword = changePasswordDTO.getNewPassword();

        User user = userService.getUserByEmailOrUserName(email);

        if (user == null) {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "Người dùng không tồn tại");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // So sánh mật khẩu hiện tại
        if (!AESUtil.decrypt(user.getPassword()).equals(currentPassword)) {
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            response.put("message", "Mật khẩu hiện tại không đúng");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        // Không cho phép mật khẩu mới trùng mật khẩu hiện tại
        if (currentPassword.equals(newPassword)) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Mật khẩu mới không được trùng với mật khẩu hiện tại");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Cập nhật mật khẩu mới
        user.setPassword(AESUtil.encrypt(newPassword));
        userService.saveUser(user);

        response.put("message", "Mật khẩu đã được thay đổi thành công");
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }


}
