package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Certificate;
import com.artostapyshyn.studlabapi.repository.CertificateRepository;
import com.artostapyshyn.studlabapi.service.CertificateService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;

    @Override
    public List<Certificate> findByStudentId(Long id) {
        return certificateRepository.findByStudentId(id);
    }

    @Override
    public Optional<Certificate> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Certificate findByName(String name) {
        return certificateRepository.findByName(name);
    }

    @Override
    public Certificate save(Certificate certificate) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}
