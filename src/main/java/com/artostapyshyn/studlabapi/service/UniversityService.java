package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.University;

import java.util.Optional;

public interface UniversityService {
    Optional <University> findById(Long id);

    University findByDomain(String domain);
}
