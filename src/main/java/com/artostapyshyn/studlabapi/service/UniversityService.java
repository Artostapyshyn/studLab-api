package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.University;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UniversityService {
    Optional <University> findById(Long id);

    University findByDomain(String domain);

    List<University> findAll();

    int countRegistered(LocalDateTime date);
}
