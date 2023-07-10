package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UniversityRepository extends JpaRepository<University, Long> {
    University findByDomain(String domain);
}