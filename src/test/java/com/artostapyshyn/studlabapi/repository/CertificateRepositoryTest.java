package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Certificate;
import com.artostapyshyn.studlabapi.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomStudent;
import static org.apache.commons.lang3.RandomUtils.nextBytes;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class CertificateRepositoryTest {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByStudentId() {
        Student student = createRandomStudent();
        entityManager.persist(student);
        Certificate certificate = new Certificate();
        certificate.setStudentId(student.getId());
        certificate.setName("Test");
        certificate.setData(nextBytes(10));
        entityManager.persist(certificate);

        Certificate certificate2 = new Certificate();
        certificate2.setStudentId(student.getId());
        certificate2.setName("Test2");
        certificate2.setData(nextBytes(10));
        entityManager.persist(certificate2);
        Iterable<Certificate> certificates = certificateRepository.findByStudentId(student.getId());
        assertEquals(2, certificates.spliterator().getExactSizeIfKnown());
    }
}