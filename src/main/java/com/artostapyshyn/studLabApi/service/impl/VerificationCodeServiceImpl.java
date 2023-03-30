package com.artostapyshyn.studLabApi.service.impl;

import com.artostapyshyn.studLabApi.entity.VerificationCode;
import com.artostapyshyn.studLabApi.repository.VerificationCodeRepository;
import com.artostapyshyn.studLabApi.service.VerificationCodeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;

    public VerificationCode generateCode(String email) {
        Optional<VerificationCode> verificationCodeOptional = Optional.ofNullable(verificationCodeRepository.findByEmail(email));
        VerificationCode verificationCode;
        if (verificationCodeOptional.isPresent()) {
            verificationCode = verificationCodeOptional.get();
        } else {
            verificationCode = new VerificationCode();
            verificationCode.setEmail(email);
        }
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        verificationCode.setCode(code);
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(15);
        verificationCode.setExpirationDate(expirationTime);
        return verificationCodeRepository.save(verificationCode);
    }

    @Override
    public VerificationCode save(VerificationCode verificationCode) {
        return verificationCodeRepository.save(verificationCode);
    }

    @Override
    public VerificationCode findByEmail(String email) {
        return verificationCodeRepository.findByEmail(email);
    }
}
