package com.artostapyshyn.studLabApi.repository;

import com.artostapyshyn.studLabApi.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByEmail(String email);
}