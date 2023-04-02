package com.artostapyshyn.studLabApi.service;

import com.artostapyshyn.studLabApi.entity.VerificationCode;

import java.util.Optional;

public interface VerificationCodeService {
    VerificationCode generateCode(String email);

    VerificationCode save(VerificationCode verificationCode);
    Optional<VerificationCode> findByStudentId(Long id);
}
