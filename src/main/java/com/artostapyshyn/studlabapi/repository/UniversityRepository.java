package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface UniversityRepository extends JpaRepository<University, Long> {
    @Query("SELECT u FROM University u WHERE u.domain = :domain")
    University findByDomain(String domain);

    @Query("SELECT u FROM University u WHERE u.registrationDate > :date")
    int countRegistered(LocalDateTime date);
}