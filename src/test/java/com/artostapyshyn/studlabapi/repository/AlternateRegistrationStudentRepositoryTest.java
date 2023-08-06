package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.AlternateRegistrationStudent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class AlternateRegistrationStudentRepositoryTest {

    @Autowired
    private AlternateRegistrationStudentRepository alternateRegistrationStudentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testExistsByCode_ExistingCode_ReturnsTrue() {
        String existingCode = "ABC123";
        AlternateRegistrationStudent student = new AlternateRegistrationStudent();
        student.setCode(existingCode);
        student.setEmail("Test@gmail.com");
        entityManager.persist(student);

        boolean exists = alternateRegistrationStudentRepository.existsByCode(existingCode);
        assertTrue(exists);
    }

    @Test
    public void testExistsByCode_NonExistingCode_ReturnsFalse() {
        String nonExistingCode = "XYZ456";

        boolean exists = alternateRegistrationStudentRepository.existsByCode(nonExistingCode);
        assertFalse(exists);
    }

    @Test
    public void testFindByCode_ExistingCode_ReturnsStudent() {
        String existingCode = "ABC123";
        AlternateRegistrationStudent student = new AlternateRegistrationStudent();
        student.setCode(existingCode);
        student.setEmail("Test@gmail.com");
        entityManager.persist(student);

        Optional<AlternateRegistrationStudent> result = alternateRegistrationStudentRepository.findByCode(existingCode);
        assertTrue(result.isPresent());
        assertEquals(existingCode, result.get().getCode());
    }

    @Test
    public void testFindByCode_NonExistingCode_ReturnsEmptyOptional() {
        String nonExistingCode = "XYZ456";

        Optional<AlternateRegistrationStudent> result = alternateRegistrationStudentRepository.findByCode(nonExistingCode);
        assertFalse(result.isPresent());
    }
}
