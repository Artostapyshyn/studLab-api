package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.Vacancy;

import java.util.List;
import java.util.Optional;

public interface VacancyService {
    List<Vacancy> findAll();

    Optional<Vacancy> findVacancyById(Long id);

    Vacancy save(Vacancy vacancy);

    void deleteById(Long id);
}

