package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.Certificate;

import java.util.List;
import java.util.Optional;

public interface CertificateService {
    List<Certificate> findByStudentId(Long id);

    Optional<Certificate> findById(Long id);

    Certificate findByName(String name);

    Certificate save(Certificate certificate);

    void deleteById(Long id);
}
