package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    List<Vacancy> findAll();

    Vacancy findVacancyById(Long id);
}