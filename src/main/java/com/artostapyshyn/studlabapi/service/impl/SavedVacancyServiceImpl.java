package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.SavedVacancy;
import com.artostapyshyn.studlabapi.repository.SavedVacancyRepository;
import com.artostapyshyn.studlabapi.service.SavedVacancyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Override
    public SavedVacancy save(SavedVacancy savedVacancy) {
        return savedVacancyRepository.save(savedVacancy);
    }

    @Transactional
    @Override
    public void delete(SavedVacancy savedVacancy) {
        savedVacancyRepository.delete(savedVacancy);
    }
}
