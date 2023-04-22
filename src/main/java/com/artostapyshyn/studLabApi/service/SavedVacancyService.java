package com.artostapyshyn.studLabApi.service;

import com.artostapyshyn.studLabApi.entity.SavedVacancy;

import java.util.List;

public interface SavedVacancyService {
    List<SavedVacancy> findByStudentId(Long studentId);

    SavedVacancy findByStudentIdAndVacancyId(Long studentId, Long vacancyId);

    SavedVacancy save(SavedVacancy savedVacancy);

    void delete(SavedVacancy savedVacancy);
}
