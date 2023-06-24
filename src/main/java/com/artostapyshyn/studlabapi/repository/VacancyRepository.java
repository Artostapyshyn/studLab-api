package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    List<Vacancy> findAll();

    Vacancy findVacancyById(Long id);
}