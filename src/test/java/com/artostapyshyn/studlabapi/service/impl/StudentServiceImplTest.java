package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.repository.StudentRepository;
import com.artostapyshyn.studlabapi.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomStudent;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
class StudentServiceImplTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
    }

    @Test
    void findById_ExistingId_ReturnsStudent() {
        Student student = createRandomStudent();
        Student savedStudent = studentRepository.save(student);

        Optional<Student> result = studentService.findById(savedStudent.getId());

        assertTrue(result.isPresent());
        assertEquals(savedStudent.getId(), result.get().getId());
    }

    @Test
    void findByEmail_ExistingEmail_ReturnsStudent() {
        Student student = createRandomStudent();
        Student savedStudent = studentRepository.save(student);

        Student result = studentService.findByEmail(savedStudent.getEmail());
        assertNotNull(result);
        assertEquals(savedStudent.getEmail(), result.getEmail());
    }

    @Test
    void save_NewStudent_ReturnsSavedStudent() {
        Student student = createRandomStudent();
        Student result = studentService.save(student);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(student.getEmail(), result.getEmail());
    }

    @Test
    void findAll_ReturnsListOfStudents() {
        Student student1 = createRandomStudent();
        Student student2 = createRandomStudent();
        studentRepository.saveAll(Arrays.asList(student1, student2));

        List<Student> result = studentService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void deleteById_ExistingId_DeletesStudent() {
        Student student = createRandomStudent();
        Student savedStudent = studentRepository.save(student);

        studentService.deleteById(savedStudent.getId());

        Optional<Student> result = studentRepository.findById(savedStudent.getId());
        assertFalse(result.isPresent());
    }

    @Test
    void findByFirstNameAndLastName_ExistingName_ReturnsStudent() {
        Student student = createRandomStudent();
        studentRepository.save(student);

        Student result = studentService.findByFirstNameAndLastName(student.getFirstName(), student.getLastName());

        assertNotNull(result);
        assertEquals(student.getFirstName(), result.getFirstName());
        assertEquals(student.getLastName(), result.getLastName());
    }

    @Test
    void countByEnabled_EnabledStudents_ReturnsCount() {
        Student student1 = createRandomStudent();
        student1.setEnabled(true);
        Student student2 = createRandomStudent();
        student2.setEnabled(false);
        studentRepository.saveAll(Arrays.asList(student1, student2));

        int result = studentService.countByEnabled(true);

        assertNotEquals(2, result);
        assertEquals(1, result);
    }

    @Test
    void getRegistrationData_ReturnsMonthlyRegistrations() {
        Student student1 = createRandomStudent();
        student1.setRegistrationDate(LocalDateTime.of(2022, 1, 10, 0, 0));
        Student student2 = createRandomStudent();
        student2.setRegistrationDate(LocalDateTime.of(2022, 1, 20, 0, 0));
        studentRepository.saveAll(Arrays.asList(student1, student2));

        Map<String, Integer> result = studentService.getRegistrationData();

        assertNotNull(result);
        assertEquals(2, result.get("січень").intValue());
    }

    @Test
    void getAuthStudentId_Authentication_ReturnsStudentId() {
        Student student = createRandomStudent();
        studentRepository.save(student);

        Authentication authentication = new UsernamePasswordAuthenticationToken(student.getEmail(), "password");

        Long result = studentService.getAuthStudentId(authentication);
        assertEquals(student.getId(), result);
    }

    @Test
    void updateStudent_ExistingStudent_UpdatesFields() {
        Student student = createRandomStudent();
        studentRepository.save(student);

        Student updatedStudent = student;
        updatedStudent.setFirstName("Updated");
        updatedStudent.setLastName("Student");
        studentRepository.save(updatedStudent);

        studentService.updateStudent(student, updatedStudent);

        Student result = studentRepository.findById(student.getId()).orElse(null);
        assertNotNull(result);
        assertEquals(updatedStudent.getFirstName(), result.getFirstName());
        assertEquals(updatedStudent.getLastName(), result.getLastName());
    }
}