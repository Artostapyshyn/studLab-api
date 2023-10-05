package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Vacancy;
import com.artostapyshyn.studlabapi.service.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;

@Log4j2
@Validated
@RestController
@RequestMapping("/api/v1/vacancies")
@CrossOrigin(maxAge = 3600, origins = "*")
@AllArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    @Operation(summary = "Get all vacancies")
    @GetMapping("/all")
    public ResponseEntity<List<Vacancy>> getAllVacancies(){
        List<Vacancy> vacancies = vacancyService.findAll();
        return ResponseEntity.ok(vacancies);
    }

    @Operation(summary = "Add a vacancy.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<Vacancy> addVacancy(@RequestBody @NotNull Vacancy vacancy, @RequestParam("image") MultipartFile image) {
        Vacancy savedVacancy = vacancyService.save(vacancy);
        return ResponseEntity.ok(savedVacancy);
    }

    @Operation(summary = "Edit a vacancy.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/edit")
    public ResponseEntity<Vacancy> editVacancy(@RequestParam("vacancyId") Long vacancyId, @RequestBody @NotNull Vacancy vacancy) {
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
            return ResponseEntity.ok(updatedVacancy);
        } else {
            return ResponseEntity.notFound().build();
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
            response.put(MESSAGE, "Vacancy deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
