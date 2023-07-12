package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.*;
import com.artostapyshyn.studlabapi.service.SavedVacancyService;
import com.artostapyshyn.studlabapi.service.StudentService;
import com.artostapyshyn.studlabapi.service.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class SavedVacancyController {

    private final StudentService studentService;

    private final SavedVacancyService savedVacancyService;

    private final VacancyService vacancyService;

    @Operation(summary = "Save vacancy")
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveVacancy(@RequestParam("vacancyId") Long vacancyId, Authentication authentication) {
        Long studentId = studentService.getAuthStudentId(authentication);
        Optional<Vacancy> optionalVacancy = vacancyService.findVacancyById(vacancyId);
        if (optionalVacancy.isPresent()) {
            SavedVacancy savedVacancy = new SavedVacancy();
            savedVacancy.setVacancy(optionalVacancy.get());
            savedVacancy.setStudentId(studentId);
            savedVacancyService.save(savedVacancy);

            Map<String, Object> response = new HashMap<>();
            response.put(CODE, "200");
            response.put(STATUS, "success");
            response.put(MESSAGE, "Vacancy saved successfully");

            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put(CODE, "404");
            response.put(STATUS, "error");
            response.put(MESSAGE, "Vacancy not found");

            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Remove vacancy from saved")
    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeSavedVacancy(Authentication authentication, @RequestParam("vacancyId") Long vacancyId) {
        Long studentId = studentService.getAuthStudentId(authentication);
        SavedVacancy savedVacancy = savedVacancyService.findByStudentIdAndVacancyId(studentId, vacancyId);
        if (savedVacancy != null) {
            savedVacancyService.delete(savedVacancy);

            Map<String, Object> response = new HashMap<>();
            response.put(CODE, "200");
            response.put(STATUS, "success");
            response.put(MESSAGE, "Vacancy removed from saved successfully");

            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put(CODE, "404");
            response.put(STATUS, "error");
            response.put(MESSAGE, "Saved vacancy not found");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "Get student saved vacancies")
    @GetMapping("/saved")
    public ResponseEntity<Map<String, Object>> getSavedVacanciesByStudentId(Authentication authentication) {
        Long studentId = studentService.getAuthStudentId(authentication);
        List<SavedVacancy> savedVacancies = savedVacancyService.findByStudentId(studentId);
        List<Vacancy> vacancies = savedVacancies.stream()
                .map(SavedVacancy::getVacancy)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put(CODE, "200");
        response.put(STATUS, "success");
        response.put(MESSAGE, "Saved vacancies retrieved successfully");
        response.put("vacancies", vacancies);

        return ResponseEntity.ok(response);
    }
}
