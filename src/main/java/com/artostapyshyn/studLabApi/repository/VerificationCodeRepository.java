package com.artostapyshyn.studLabApi.repository;

import com.artostapyshyn.studLabApi.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByStudentId(Long id);
}
