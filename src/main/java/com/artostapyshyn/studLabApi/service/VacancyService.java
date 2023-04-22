package com.artostapyshyn.studLabApi.service;

import com.artostapyshyn.studLabApi.entity.Vacancy;

import java.util.List;

public interface VacancyService {
    List<Vacancy> findAll();

    Vacancy findVacancyById(Long id);

    Vacancy save(Vacancy vacancy);
}
