package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.*;
import com.artostapyshyn.studlabapi.service.SavedVacancyService;
import com.artostapyshyn.studlabapi.service.StudentService;
import com.artostapyshyn.studlabapi.service.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;

@RestController
@RequestMapping("/api/v1/savedVacancies")
@CrossOrigin(maxAge = 3600, origins = "*")
@AllArgsConstructor
public class SavedVacancyController {

    private final StudentService studentService;

    private final SavedVacancyService savedVacancyService;

    private final VacancyService vacancyService;

    @Operation(summary = "Save vacancy",
            security = @SecurityRequirement(name = "basicAuth"))
    @PostMapping("/save")
    public ResponseEntity<SavedVacancy> saveVacancy(@RequestParam("vacancyId") Long vacancyId, Authentication authentication) {
        Long studentId = studentService.getAuthStudentId(authentication);
        Optional<Vacancy> optionalVacancy = vacancyService.findVacancyById(vacancyId);
        if (optionalVacancy.isPresent()) {
            SavedVacancy savedVacancy = new SavedVacancy();
            savedVacancy.setVacancy(optionalVacancy.get());
            savedVacancy.setStudentId(studentId);
            savedVacancyService.save(savedVacancy);
            return ResponseEntity.ok(savedVacancy);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Remove vacancy from saved",
            security = @SecurityRequirement(name = "basicAuth"))
    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeSavedVacancy(Authentication authentication, @RequestParam("vacancyId") Long vacancyId) {
        Long studentId = studentService.getAuthStudentId(authentication);
        SavedVacancy savedVacancy = savedVacancyService.findByStudentIdAndVacancyId(studentId, vacancyId);
        if (savedVacancy != null) {
            savedVacancyService.delete(savedVacancy);

            Map<String, Object> response = new HashMap<>();
            response.put(MESSAGE, "Vacancy removed from saved successfully");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get student saved vacancies",
            security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/saved")
    public ResponseEntity<List<Vacancy>> getSavedVacanciesByStudentId(Authentication authentication) {
        Long studentId = studentService.getAuthStudentId(authentication);
        List<SavedVacancy> savedVacancies = savedVacancyService.findByStudentId(studentId);
        List<Vacancy> vacancies = savedVacancies.stream()
                .map(SavedVacancy::getVacancy)
                .toList();

        return ResponseEntity.ok(vacancies);
    }
}
