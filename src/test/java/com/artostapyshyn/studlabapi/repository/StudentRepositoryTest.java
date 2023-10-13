package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Student;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomStudent;

@ActiveProfiles("test")
@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAll() {
        Student student1 = createRandomStudent();
        student1.setFirstName("Bob");

        Student student2 = createRandomStudent();
        student2.setFirstName("Sarah");
        entityManager.persist(student1);
        entityManager.persist(student2);
        entityManager.flush();

        List<Student> students = studentRepository.findAll();

        Assertions.assertEquals(2, students.size());
        Assertions.assertEquals("Bob", students.get(0).getFirstName());
        Assertions.assertEquals("Sarah", students.get(1).getFirstName());
    }

    @Test
    void findByEmail() {
        Student student = createRandomStudent();
        student.setEmail("test@gmail.com");
        entityManager.persist(student);
        entityManager.flush();

        Student result = studentRepository.findByEmail("test@gmail.com");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("test@gmail.com", result.getEmail());
        Assertions.assertNotEquals("test@exampl;e.com", result.getEmail());
    }

    @Test
    void findById() {
        Student student = createRandomStudent();
        entityManager.persist(student);
        entityManager.flush();

        Optional<Student> result = studentRepository.findById(student.getId());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(student.getId(), result.get().getId());
    }

    @Test
    void save() {
        Student student = createRandomStudent();

        Student result = studentRepository.save(student);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());

        Student savedStudent = entityManager.find(Student.class, result.getId());
        Assertions.assertNotNull(savedStudent);
        Assertions.assertEquals(result.getId(), savedStudent.getId());
    }

    @Test
    void deleteById() {
        Student student = createRandomStudent();
        entityManager.persist(student);
        entityManager.flush();

        studentRepository.deleteById(student.getId());

        Student deletedStudent = entityManager.find(Student.class, student.getId());
        Assertions.assertNull(deletedStudent);
    }

    @Test
    void findByFirstNameAndLastName() {
        String firstName = "John";
        String lastName = "Doe";

        Student student = createRandomStudent();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        entityManager.persist(student);
        entityManager.flush();

        Student result = studentRepository.findByFirstAndLastName(firstName, lastName);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(firstName, result.getFirstName());
        Assertions.assertEquals(lastName, result.getLastName());
    }

    @Test
    void countByEnabled(){
        Student student1 = createRandomStudent();
        Student student2 = createRandomStudent();
        Student student3 = createRandomStudent();
        entityManager.persist(student1);
        entityManager.persist(student2);
        entityManager.persist(student3);
        entityManager.flush();

        student1.setEnabled(false);
        int count = studentRepository.countByEnabled(true, LocalDateTime.of(2023,8,20,0,0,0));

        Assertions.assertEquals(2, count);
        Assertions.assertNotEquals(0, count);
    }

}
