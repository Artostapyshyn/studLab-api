package com.artostapyshyn.studLabApi.service;

import com.artostapyshyn.studLabApi.entity.VerificationCodes;


import java.util.Optional;

public interface VerificationCodesService {
    VerificationCodes generateCode(String email);

    VerificationCodes save(VerificationCodes verificationCodes);
    Optional<VerificationCodes> findByStudentId(Long id);

    void deleteExpiredTokens();
}
