package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.VerificationCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomVerificationCode;

@ActiveProfiles("test")
@DataJpaTest
class VerificationCodeRepositoryTest {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByEmail() {
        VerificationCode verificationCode = createRandomVerificationCode();
        entityManager.persist(verificationCode);
        entityManager.flush();

        VerificationCode result = verificationCodeRepository.findByEmail(verificationCode.getEmail());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(verificationCode.getEmail(), result.getEmail());
    }

    @Test
    void findByStudentId() {
        VerificationCode verificationCode = createRandomVerificationCode();
        entityManager.persist(verificationCode);
        entityManager.flush();

        Optional<VerificationCode> result = verificationCodeRepository.findByStudentId(verificationCode.getStudentId());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(verificationCode.getStudentId(), result.get().getStudentId());
    }
}