package com.artostapyshyn.studLabApi.service.impl;

import com.artostapyshyn.studLabApi.entity.Student;
import com.artostapyshyn.studLabApi.entity.VerificationCode;
import com.artostapyshyn.studLabApi.repository.VerificationCodeRepository;
import com.artostapyshyn.studLabApi.service.StudentService;
import com.artostapyshyn.studLabApi.service.VerificationCodeService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;

    private final StudentService studentService;

    public VerificationCode generateCode(String email) {
        Student student = studentService.findByEmail(email);
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(generateRandomCode());
        verificationCode.setStudentId(student.getId());
        verificationCode.setExpirationDate(LocalDateTime.now().plusMinutes(15));
        return verificationCodeRepository.save(verificationCode);
    }

    private int generateRandomCode() {
        Random random = new Random();
        return random.nextInt(900000) + 100000;
    }

    @Override
    public VerificationCode save(VerificationCode verificationCode) {
        return verificationCodeRepository.save(verificationCode);
    }

    @Override
    public Optional<VerificationCode> findByStudentId(Long id) {
        return verificationCodeRepository.findById(id);
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional
    @Override
    public void deleteExpiredTokens() {
        verificationCodeRepository.deleteExpiredTokens();
    }
}
