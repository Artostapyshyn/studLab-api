package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.entity.VerificationCode;
import com.artostapyshyn.studlabapi.repository.VerificationCodesRepository;
import com.artostapyshyn.studlabapi.service.StudentService;
import com.artostapyshyn.studlabapi.service.VerificationCodesService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable("verificationCodesByEmail")
    public VerificationCode generateCode(String email) {
        Student student = studentService.findByEmail(email);
        int verificationCodeValue = generateRandomCode();
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(verificationCodeValue);
        verificationCode.setStudentId(student.getId());
        verificationCode.setEmail(email);
        verificationCode.setExpirationDate(LocalDateTime.now().plusMinutes(1));
        verificationCode.setLastSentTime(LocalDateTime.now());
        return verificationCodesRepository.save(verificationCode);
    }

    private int generateRandomCode() {
        Random random = new Random();
        return random.nextInt(900000) + 100000;
    }

    @Override
    public VerificationCode save(VerificationCode verificationCode) {
        return verificationCodesRepository.save(verificationCode);
    }

    @Override
    public Optional<VerificationCode> findByStudentId(Long id) {
        return verificationCodesRepository.findByStudentId(id);
    }

    @Override
    public VerificationCode findByEmail(String email) {
        return verificationCodesRepository.findByEmail(email);
    }

    @Scheduled(fixedRate = 90000000)
    @Transactional
    @Override
    public void deleteExpiredTokens() {
        verificationCodesRepository.deleteExpiredTokens();
    }
}
