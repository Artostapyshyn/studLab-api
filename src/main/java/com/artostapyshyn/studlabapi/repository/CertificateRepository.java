package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByStudentId(Long id);

    Certificate findByName(String name);

    boolean existsByStudentIdAndName(Long id, String name);
}