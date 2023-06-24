package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationCodesRepository extends JpaRepository<VerificationCode, Long> {
    @Modifying
    @Query("DELETE FROM VerificationCode t WHERE t.expirationDate < CURRENT DATE")
    void deleteExpiredTokens();

    VerificationCode findByEmail(String email);

    Optional<VerificationCode> findByStudentId(Long id);
}
