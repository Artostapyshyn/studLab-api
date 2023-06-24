package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByEmail(String email);

    Optional<Student> findById(Long id);

    Student findByFirstNameAndLastName(String firstName, String lastName);

    int countByEnabled(boolean enabled);
}