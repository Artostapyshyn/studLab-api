package com.artostapyshyn.studLabApi.service.impl;

import com.artostapyshyn.studLabApi.entity.SavedVacancy;
import com.artostapyshyn.studLabApi.repository.SavedVacancyRepository;
import com.artostapyshyn.studLabApi.service.SavedVacancyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SavedVacancyServiceImpl implements SavedVacancyService {

    private final SavedVacancyRepository savedVacancyRepository;

    @Override
    public List<SavedVacancy> findByStudentId(Long studentId) {
        return savedVacancyRepository.findByStudentId(studentId);
    }

    @Override
    public SavedVacancy findByStudentIdAndVacancyId(Long studentId, Long vacancyId) {
        return savedVacancyRepository.findByStudentIdAndVacancyId(studentId, vacancyId);
    }

    @Override
    public SavedVacancy save(SavedVacancy savedVacancy) {
        return savedVacancyRepository.save(savedVacancy);
    }

    @Override
    public void delete(SavedVacancy savedVacancy) {
        savedVacancyRepository.delete(savedVacancy);
    }
}
