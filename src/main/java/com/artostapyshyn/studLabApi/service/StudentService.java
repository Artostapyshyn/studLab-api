package com.artostapyshyn.studLabApi.service;

import com.artostapyshyn.studLabApi.entity.Student;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    Optional<Student> findById(Long id);

    Student findByEmail(String email);

    Student save(Student student);

    List<Student> findAll();

    void deleteById(Long id);
}
