package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.service.EventService;
import com.artostapyshyn.studlabapi.service.StudentService;
import com.artostapyshyn.studlabapi.service.UniversityService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/statistic")
@CrossOrigin(origins = "*")
@AllArgsConstructor
@Log4j2
public class StatisticController {

    private final StudentService studentService;

    private final UniversityService universityService;

    private final EventService eventService;

    @GetMapping("/registered-amount")
    public ResponseEntity<Map<String, Object>> getTotalEnabledUsers() {
        Map<String, Object> response = new HashMap<>();
        int totalEnabledUsers = studentService.countByEnabled(true);
        response.put("total", totalEnabledUsers);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/registration-data")
    public ResponseEntity<Map<String, Integer>> getRegistrationData() {
        try {
            Map<String, Integer> registrationData = studentService.getRegistrationData();
            return ResponseEntity.ok(registrationData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/universities")
    public ResponseEntity<Map<String, Object>> getTotalUniversities() {
        Map<String, Object> response = new HashMap<>();
        int totalUniversities = universityService.findAll().size();
        response.put("total", totalUniversities);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/created-events")
    public ResponseEntity<Map<String, Object>> getTotalCreatedEvents() {
        Map<String, Object> response = new HashMap<>();
        int totalCreatedEvents = eventService.getCreatedEventCount();
        response.put("total", totalCreatedEvents);
        return ResponseEntity.ok(response);
    }
}
