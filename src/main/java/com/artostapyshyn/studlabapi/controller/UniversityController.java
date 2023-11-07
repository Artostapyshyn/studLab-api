package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.University;
import com.artostapyshyn.studlabapi.service.UniversityService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/universities")
public class UniversityController {

    private final UniversityService universityService;

    @Operation(summary = "Get all universities.")
    @GetMapping("/all")
    public ResponseEntity<List<University>> getActiveUniversities() {
        List<University> universities = universityService.findActiveUniversities(LocalDateTime.of(2023, 9, 1,0,0));
        return ResponseEntity.ok(universities);
    }
}
