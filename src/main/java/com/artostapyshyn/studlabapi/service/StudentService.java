package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.Student;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StudentService {
    Optional<Student> findById(Long id);

    Student findByEmail(String email);

    Student save(Student student);

    List<Student> findAll();

    void deleteById(Long id);

    Student findByFirstNameAndLastName(String firstName, String lastName);

    int countByEnabled(boolean enabled);

    Map<String, Integer> getRegistrationData();

    Long getAuthStudentId(Authentication authentication);

    void updateStudent(Student existingStudent, Student updatedStudent);
}
