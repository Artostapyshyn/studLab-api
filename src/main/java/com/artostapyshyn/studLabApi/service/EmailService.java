package com.artostapyshyn.studLabApi.service;

public interface EmailService {
    void sendVerificationCode(String toEmail, String code);
}
