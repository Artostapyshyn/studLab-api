package com.artostapyshyn.studlabapi.service;

public interface EmailService {
    void sendVerificationCode(String toEmail, int code);

    void sendResetPasswordCode(String toEmail, int code);
}
