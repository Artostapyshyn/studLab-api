package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByEmail(String email);

    @Query("SELECT s FROM Student s WHERE s.password IS NOT NULL AND s.firstName = :firstName AND s.lastName = :lastName")
    Student findByFirstAndLastName(@Param("firstName") String firstName, @Param("lastName") String lastName);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.enabled = :enabled AND s.registrationDate > :date")
    int countByEnabled(boolean enabled, LocalDateTime date);

    List<Student> findByFirstNameContaining(String firstName);

    List<Student> findByLastNameContaining(String lastName);

    List<Student> findByFirstNameContainingAndLastNameContaining(String firstName, String lastName);
}