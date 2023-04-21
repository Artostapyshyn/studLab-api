package com.artostapyshyn.studLabApi.service;

import com.artostapyshyn.studLabApi.entity.University;

import java.util.Optional;

public interface UniversityService {
    Optional <University> findById(Long id);

    University findByDomain(String domain);
}
