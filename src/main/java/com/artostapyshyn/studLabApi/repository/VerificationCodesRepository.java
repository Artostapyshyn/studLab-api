package com.artostapyshyn.studLabApi.repository;

import com.artostapyshyn.studLabApi.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VerificationCodesRepository extends JpaRepository<VerificationCode, Long> {
    @Modifying
    @Query("DELETE FROM VerificationCode t WHERE t.expirationDate < NOW()")
    void deleteExpiredTokens();

    VerificationCode findByEmail(String email);

    Optional<VerificationCode> findByStudentId(Long id);
}
