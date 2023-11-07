package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UniversityRepository extends JpaRepository<University, Long> {
    @Query("SELECT u FROM University u WHERE u.domain = :domain")
    University findByDomain(String domain);

    @Query("SELECT COUNT(u) FROM University u WHERE u.registrationDate > :date")
    int countRegistered(LocalDateTime date);

    @Query("SELECT u FROM University u WHERE u.registrationDate > :date")
    List<University> findActiveUniversities(@Param("date") LocalDateTime date);

    University findByName(String name);
}