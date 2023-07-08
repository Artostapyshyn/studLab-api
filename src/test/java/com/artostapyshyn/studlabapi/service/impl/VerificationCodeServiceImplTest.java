package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.entity.VerificationCode;
import com.artostapyshyn.studlabapi.repository.VerificationCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomStudent;
import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomVerificationCode;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
class VerificationCodeServiceImplTest {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Autowired
    private VerificationCodeServiceImpl verificationCodeService;

    @Autowired
    private StudentServiceImpl studentService;

    @BeforeEach
    void setUp() {
        verificationCodeRepository.deleteAll();
    }

    @Test
    void generateCode() {
        Student student = createRandomStudent();
        studentService.save(student);

        VerificationCode result = verificationCodeService.generateCode(student.getEmail());

        assertNotNull(result);
        assertEquals(student.getEmail(), result.getEmail());
    }

    @Test
    void save() {
        VerificationCode verificationCode = createRandomVerificationCode();

        VerificationCode savedCode = verificationCodeService.save(verificationCode);

        assertNotNull(savedCode);
        assertNotNull(savedCode.getId());
        assertEquals(verificationCode.getEmail(), savedCode.getEmail());
    }

    @Test
    void findByStudentId() {
        VerificationCode verificationCode = createRandomVerificationCode();
        verificationCodeRepository.save(verificationCode);
        Optional<VerificationCode> foundCode = verificationCodeService.findByStudentId(verificationCode.getStudentId());

        assertTrue(foundCode.isPresent());
        assertEquals(verificationCode.getId(), foundCode.get().getId());
    }

    @Test
    void findByEmail() {
        VerificationCode verificationCode = createRandomVerificationCode();
        verificationCodeRepository.save(verificationCode);

        Optional<VerificationCode> foundCode = Optional.ofNullable(verificationCodeService.findByEmail(verificationCode.getEmail()));

        assertTrue(foundCode.isPresent());
        assertEquals(verificationCode.getId(), foundCode.get().getId());
    }

    @Test
    void deleteExpiredTokens() {
        VerificationCode expiredCode1 = createRandomVerificationCode();
        expiredCode1.setExpirationDate(LocalDateTime.now().minusDays(1));
        VerificationCode expiredCode2 = createRandomVerificationCode();
        expiredCode2.setExpirationDate(LocalDateTime.now().minusDays(2));
        verificationCodeRepository.save(expiredCode1);
        verificationCodeRepository.save(expiredCode2);

        verificationCodeService.deleteExpiredTokens();

        assertFalse(verificationCodeRepository.existsById(expiredCode1.getId()));
        assertFalse(verificationCodeRepository.existsById(expiredCode2.getId()));
    }
}
