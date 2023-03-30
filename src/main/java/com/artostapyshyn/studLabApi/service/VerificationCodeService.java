package com.artostapyshyn.studLabApi.service;

import com.artostapyshyn.studLabApi.entity.VerificationCode;

public interface VerificationCodeService {
    VerificationCode generateCode(String email);

    VerificationCode save(VerificationCode verificationCode);
    VerificationCode findByEmail(String email);
}
