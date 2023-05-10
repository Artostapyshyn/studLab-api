package com.artostapyshyn.studLabApi.controller;

import com.artostapyshyn.studLabApi.entity.Vacancy;
import com.artostapyshyn.studLabApi.service.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@CrossOrigin(origins = "https://stud-lab-api.onrender.com", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/vacancies")
@AllArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    @Operation(summary = "Get all vacancies")
    @GetMapping
    public ResponseEntity<List<Object>> getAllVacancies(){
        List<Object> response = new ArrayList<>();
        response.add(vacancyService.findAll());

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Add a vacancy.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<?> addVacancy(@RequestBody Vacancy vacancy, @RequestParam("image") MultipartFile image) {
        Vacancy savedVacancy = vacancyService.save(vacancy);
        log.info("New vacancy added with id - " + savedVacancy.getId());
        return ResponseEntity.ok(savedVacancy);
    }

    @Operation(summary = "Edit a vacancy.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/edit")
    public ResponseEntity<?> addVacancy(@RequestParam("vacancyId") Long vacancyId, @RequestParam Vacancy vacancy) {
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
    @DeleteMapping ("/delete")
    public ResponseEntity<?> deleteVacancy(@RequestParam("vacancyId") Long vacancyId) {
        Optional<Vacancy> vacancy = vacancyService.findVacancyById(vacancyId);
        if (vacancy.isPresent()) {
            vacancyService.deleteById(vacancyId);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
