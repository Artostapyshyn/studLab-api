package com.artostapyshyn.studLabApi.repository;

import com.artostapyshyn.studLabApi.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByEmail(String email);

    Optional<Student> findById(Long id);
}