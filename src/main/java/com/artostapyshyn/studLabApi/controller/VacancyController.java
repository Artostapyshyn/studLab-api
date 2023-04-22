package com.artostapyshyn.studLabApi.controller;

import com.artostapyshyn.studLabApi.service.VacancyService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "https://stud-lab-api.onrender.com", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/vacancies")
@AllArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    @GetMapping
    public ResponseEntity<List<Object>> getAllVacancies(){
        List<Object> response = new ArrayList<>();
        response.add(vacancyService.findAll());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
