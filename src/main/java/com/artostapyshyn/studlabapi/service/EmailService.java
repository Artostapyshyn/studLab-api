package com.artostapyshyn.studlabapi.service;

public interface EmailService {
    void sendVerificationCode(String toEmail, int code);
}
