package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.University;
import com.artostapyshyn.studlabapi.repository.UniversityRepository;
import com.artostapyshyn.studlabapi.service.UniversityService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class UniversityServiceImpl implements UniversityService {

    private final UniversityRepository universityRepository;

    @Override
    @Cacheable("universityById")
    public Optional<University> findById(Long id) {
        return universityRepository.findById(id);
    }

    @Override
    @Cacheable("universityByDomain")
    public University findByDomain(String domain) {
        return universityRepository.findByDomain(domain);
    }

    @Override
    @Cacheable("allUniversities")
    public List<University> findAll() {
        return universityRepository.findAll();
    }
}
