package com.artostapyshyn.studLabApi.controller;

import com.artostapyshyn.studLabApi.entity.*;
import com.artostapyshyn.studLabApi.service.SavedVacancyService;
import com.artostapyshyn.studLabApi.service.StudentService;
import com.artostapyshyn.studLabApi.service.VacancyService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vacancies")
@AllArgsConstructor
public class VacancyController {

    private final StudentService studentService;

    private final SavedVacancyService savedVacancyService;

    private final VacancyService vacancyService;

    @GetMapping
    public ResponseEntity<List<Object>> getAllVacancies(){
        List<Object> response = new ArrayList<>();
        response.add(vacancyService.findAll());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public SavedVacancy saveVacancy(@RequestParam("vacancyId") Long vacancyId, Authentication authentication) {
        Long studentId = getAuthStudentId(authentication);
        Vacancy vacancy = vacancyService.findVacancyById(vacancyId);
        SavedVacancy savedVacancy = new SavedVacancy();
        savedVacancy.setVacancy(vacancy);
        savedVacancy.setStudentId(studentId);
        return savedVacancyService.save(savedVacancy);
    }

    @DeleteMapping
    public void removeSavedVacancy(Authentication authentication, @RequestParam("vacancyId") Long vacancyId) {
        Long studentId = getAuthStudentId(authentication);
        SavedVacancy savedVacancy = savedVacancyService.findByStudentIdAndVacancyId(studentId, vacancyId);
        savedVacancyService.delete(savedVacancy);
    }

    private Long getAuthStudentId(Authentication authentication) {
        String studentEmail = authentication.getName();
        Student student = studentService.findByEmail(studentEmail);
        return student.getId();
    }

    @GetMapping
    public List<Vacancy> getFavouriteEventsByUserId(Authentication authentication) {
        Long studentId = getAuthStudentId(authentication);
        List<SavedVacancy> savedVacancies = savedVacancyService.findByStudentId(studentId);
        return savedVacancies.stream()
                .map(SavedVacancy::getVacancy)
                .toList();
    }

}
