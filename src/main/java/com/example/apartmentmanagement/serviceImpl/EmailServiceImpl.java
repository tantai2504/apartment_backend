package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Override
    public void sendRegistrationOtpEmail(String to, String otp) {
        String subject = "OTP Đăng ký tài khoản";
        String text = "Cảm ơn bạn đã đăng ký.\nMã OTP của bạn là: " + otp;
        sendEmail(to, subject, text);
    }
    @Override
    public void sendForgotPasswordOtpEmail(String to, String otp) {
        String subject = "OTP Khôi phục mật khẩu";
        String text = "Bạn đã yêu cầu đặt lại mật khẩu.\nMã OTP của bạn là: " + otp;
        sendEmail(to, subject, text);
    }
    @Override
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
    @Override
    public void sendVerificationEmail(String to, String userName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Thông báo duyệt cư dân");
        message.setText("Xin chúc mừng " + userName + " đã trở thành cư dân của căn hộ.\n\n" +
                "Vui lòng liên hệ ban quản lý nếu có bất kỳ thắc mắc nào.");
        mailSender.send(message);
    }
}
