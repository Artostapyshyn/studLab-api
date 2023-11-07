package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Major;
import com.artostapyshyn.studlabapi.service.MajorService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/majors")
public class MajorController {

    private final MajorService majorService;

    @Operation(summary = "Get all majors.")
    @GetMapping("/all")
    public ResponseEntity<List<Major>> getAllMajors() {
        List<Major> majors = majorService.findAll();
        return ResponseEntity.ok(majors);
    }
}
