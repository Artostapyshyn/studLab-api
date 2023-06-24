package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UniversityRepository extends JpaRepository<University, Long> {
    Optional<University> findById(Long id);

    University findByDomain(String domain);

    List<University> findAll();
}