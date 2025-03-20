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

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp);
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
