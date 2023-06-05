package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.SavedVacancy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedVacancyRepository extends JpaRepository<SavedVacancy, Long> {
    List<SavedVacancy> findByStudentId(Long studentId);

    SavedVacancy findByStudentIdAndVacancyId(Long studentId, Long vacancyId);
}