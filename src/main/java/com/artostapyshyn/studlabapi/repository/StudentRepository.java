package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByEmail(String email);

    Student findByFirstNameAndLastName(String firstName, String lastName);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.enabled = :enabled AND s.registrationDate > :date")
    int countByEnabled(boolean enabled, LocalDateTime date);
}