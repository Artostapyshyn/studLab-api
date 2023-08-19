package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.service.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationCode(String toEmail, int code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("studlabbot@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Код підтвердження");
        message.setText("Верифікаційни код: " + code);
        mailSender.send(message);
    }

    @Override
    public void sendResetPasswordCode(String toEmail, int code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("studlabbot@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Зміна паролю");
        message.setText("Верифікаційни код: " + code);
        mailSender.send(message);
    }
}
