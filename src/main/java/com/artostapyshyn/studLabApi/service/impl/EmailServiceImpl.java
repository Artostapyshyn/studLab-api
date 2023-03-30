package com.artostapyshyn.studLabApi.service.impl;

import com.artostapyshyn.studLabApi.service.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("artostapyshyn@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Verification code");
        message.setText("Your verification code is: " + code);
        mailSender.send(message);
    }
}
