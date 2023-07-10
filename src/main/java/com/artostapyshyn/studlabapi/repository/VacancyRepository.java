package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    Vacancy findVacancyById(Long id);
}