package com.artostapyshyn.studLabApi.repository;

import com.artostapyshyn.studLabApi.entity.VerificationCodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface VerificationCodesRepository extends JpaRepository<VerificationCodes, Long> {
    @Modifying
    @Query("DELETE FROM VerificationCodes t WHERE t.expirationDate < NOW()")
    void deleteExpiredTokens();
}
