package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.University;
import com.artostapyshyn.studlabapi.repository.UniversityRepository;
import com.artostapyshyn.studlabapi.service.UniversityService;
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
