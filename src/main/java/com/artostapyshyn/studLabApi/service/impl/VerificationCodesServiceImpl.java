package com.artostapyshyn.studLabApi.service.impl;

import com.artostapyshyn.studLabApi.entity.Student;
import com.artostapyshyn.studLabApi.entity.VerificationCodes;
import com.artostapyshyn.studLabApi.repository.VerificationCodesRepository;
import com.artostapyshyn.studLabApi.service.StudentService;
import com.artostapyshyn.studLabApi.service.VerificationCodesService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class VerificationCodesServiceImpl implements VerificationCodesService {

    private final VerificationCodesRepository verificationCodesRepository;

    private final StudentService studentService;

    public VerificationCodes generateCode(String email) {
        Student student = studentService.findByEmail(email);
        VerificationCodes verificationCodes = new VerificationCodes();
        verificationCodes.setCode(generateRandomCode());
        verificationCodes.setStudentId(student.getId());
        verificationCodes.setExpirationDate(LocalDateTime.now().plusMinutes(15));
        return verificationCodesRepository.save(verificationCodes);
    }

    private int generateRandomCode() {
        Random random = new Random();
        return random.nextInt(900000) + 100000;
    }

    @Override
    public VerificationCodes save(VerificationCodes verificationCodes) {
        return verificationCodesRepository.save(verificationCodes);
    }

    @Override
    public Optional<VerificationCodes> findByStudentId(Long id) {
        return verificationCodesRepository.findById(id);
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional
    @Override
    public void deleteExpiredTokens() {
        verificationCodesRepository.deleteExpiredTokens();
    }
}
