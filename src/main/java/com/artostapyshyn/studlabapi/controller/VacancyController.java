package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Vacancy;
import com.artostapyshyn.studlabapi.service.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;

@Log4j2
@RestController
@RequestMapping("/api/v1/vacancies")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    @Operation(summary = "Get all vacancies")
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllVacancies() {
        Map<String, Object> response = new HashMap<>();
        List<Vacancy> vacancies = vacancyService.findAll();
        response.put(CODE, "200");
        response.put(STATUS, "success");
        response.put(MESSAGE, "All vacancies retrieved successfully");
        response.put("vacancies", vacancies);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Add a vacancy.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addVacancy(@RequestBody Vacancy vacancy, @RequestParam("image") MultipartFile image) {
        Vacancy savedVacancy = vacancyService.save(vacancy);

        Map<String, Object> response = new HashMap<>();
        response.put(CODE, "200");
        response.put(STATUS, "success");
        response.put(MESSAGE, "Vacancy added successfully");
        response.put("vacancy", savedVacancy);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Edit a vacancy.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/edit")
    public ResponseEntity<Map<String, Object>> editVacancy(@RequestParam("vacancyId") Long vacancyId, @RequestParam Vacancy vacancy) {
        Map<String, Object> response = new HashMap<>();
        Optional<Vacancy> optionalVacancy = vacancyService.findVacancyById(vacancyId);

        if (optionalVacancy.isPresent()) {
            Vacancy existingVacancy = optionalVacancy.get();

            if (vacancy.getNameOfVacancy() != null) {
                existingVacancy.setNameOfVacancy(vacancy.getNameOfVacancy());
            }

            if (vacancy.getDescription() != null) {
                existingVacancy.setDescription(vacancy.getDescription());
            }

            Vacancy updatedVacancy = vacancyService.save(existingVacancy);
            response.put(CODE, "200");
            response.put(STATUS, "success");
            response.put(MESSAGE, "Vacancy updated successfully");
            response.put("vacancy", updatedVacancy);

            return ResponseEntity.ok(response);
        } else {
            response.put(CODE, "404");
            response.put(STATUS, "error");
            response.put(MESSAGE, "Vacancy not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "Delete vacancy by id.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteVacancy(@RequestParam("vacancyId") Long vacancyId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Vacancy> vacancy = vacancyService.findVacancyById(vacancyId);

        if (vacancy.isPresent()) {
            vacancyService.deleteById(vacancyId);
            response.put(CODE, "200");
            response.put(STATUS, "success");
            response.put(MESSAGE, "Vacancy deleted successfully");

            return ResponseEntity.ok(response);
        } else {
            response.put(CODE, "404");
            response.put(STATUS, "error");
            response.put(MESSAGE, "Vacancy not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
