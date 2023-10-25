package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.dto.EditDto;
import com.artostapyshyn.studlabapi.dto.SignUpDto;
import com.artostapyshyn.studlabapi.entity.Student;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    Optional<Student> findById(Long id);

    Student findByEmail(String email);

    Student save(Student student);

    List<Student> findAll();

    void deleteById(Long id);

    List<Student> searchByNames(String firstName, String lastName);

    List<Student> findByFirstNameAndLastName(String firstName, String lastName);

    Student findByFirstAndLastName(String firstName, String lastName);

    Long getAuthStudentId(Authentication authentication);

    void updateStudent(Student existingStudent, EditDto updatedStudent);

    void updatePassword(Student student, String password);

    void signUpStudent(SignUpDto signUpDto, Student existingStudent);
}
