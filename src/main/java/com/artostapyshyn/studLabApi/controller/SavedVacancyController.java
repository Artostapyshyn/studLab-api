package com.artostapyshyn.studLabApi.controller;

import com.artostapyshyn.studLabApi.entity.*;
import com.artostapyshyn.studLabApi.service.SavedVacancyService;
import com.artostapyshyn.studLabApi.service.StudentService;
import com.artostapyshyn.studLabApi.service.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "https://stud-lab-api.onrender.com", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/savedVacancies")
@AllArgsConstructor
public class SavedVacancyController {

    private final StudentService studentService;

    private final SavedVacancyService savedVacancyService;

    private final VacancyService vacancyService;

    @Operation(summary = "Save vacancy")
    @PostMapping
    public SavedVacancy saveVacancy(@RequestParam("vacancyId") Long vacancyId, Authentication authentication) {
        Long studentId = getAuthStudentId(authentication);
        Optional<Vacancy> vacancy = vacancyService.findVacancyById(vacancyId);
        SavedVacancy savedVacancy = new SavedVacancy();
        savedVacancy.setVacancy(vacancy.get());
        savedVacancy.setStudentId(studentId);
        return savedVacancyService.save(savedVacancy);
    }

    @Operation(summary = "Remove vacancy from saved")
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

    @Operation(summary = "Get student saved vacancies")
    @GetMapping
    public List<Vacancy> getSavedVacanciesByStudentId(Authentication authentication) {
        Long studentId = getAuthStudentId(authentication);
        List<SavedVacancy> savedVacancies = savedVacancyService.findByStudentId(studentId);
        return savedVacancies.stream()
                .map(SavedVacancy::getVacancy)
                .toList();
    }

}
