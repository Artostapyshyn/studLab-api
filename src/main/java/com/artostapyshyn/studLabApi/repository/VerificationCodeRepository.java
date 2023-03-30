package com.artostapyshyn.studLabApi.repository;

import com.artostapyshyn.studLabApi.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    VerificationCode findByEmail(String email);
}
