package com.artostapyshyn.studLabApi.service.impl;

import com.artostapyshyn.studLabApi.entity.University;
import com.artostapyshyn.studLabApi.repository.UniversityRepository;
import com.artostapyshyn.studLabApi.service.UniversityService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@AllArgsConstructor
public class UniversityServiceImpl implements UniversityService {

    private final UniversityRepository universityRepository;
    @Override
    public Optional<University> findById(Long id) {
        return universityRepository.findById(id);
    }

    @Override
    public University findByDomain(String domain) {
        return universityRepository.findByDomain(domain);
    }
}
