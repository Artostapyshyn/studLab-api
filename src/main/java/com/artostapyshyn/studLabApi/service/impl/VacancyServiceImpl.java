package com.artostapyshyn.studLabApi.service.impl;

import com.artostapyshyn.studLabApi.entity.Vacancy;
import com.artostapyshyn.studLabApi.repository.VacancyRepository;
import com.artostapyshyn.studLabApi.service.VacancyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class VacancyServiceImpl implements VacancyService {

    private final VacancyRepository vacancyRepository;
    @Override
    public List<Vacancy> findAll() {
        return vacancyRepository.findAll();
    }

    @Override
    public Optional<Vacancy> findVacancyById(Long id) {
        return Optional.ofNullable(vacancyRepository.findVacancyById(id));
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        return vacancyRepository.save(vacancy);
    }

    @Override
    public void deleteById(Long id) {
        vacancyRepository.deleteById(id);
    }
}
