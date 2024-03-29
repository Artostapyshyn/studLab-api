package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.VerificationCode;


import java.util.Optional;

public interface VerificationCodeService {
    VerificationCode generateCode(String email);

    VerificationCode save(VerificationCode verificationCode);

    Optional<VerificationCode> findByStudentId(Long id);

    VerificationCode findByEmail(String email);

    void deleteExpiredTokens();

    void delete(VerificationCode code);
}
