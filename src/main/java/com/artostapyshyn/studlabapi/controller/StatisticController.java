package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.service.EventService;
import com.artostapyshyn.studlabapi.service.StudentStatisticsService;
import com.artostapyshyn.studlabapi.service.UniversityService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;

@RestController
@RequestMapping("/api/v1/statistic")
@CrossOrigin(maxAge = 3600, origins = "*")
@AllArgsConstructor
@Log4j2
public class StatisticController {

    private final StudentStatisticsService studentStatisticsService;

    private final UniversityService universityService;

    private final EventService eventService;

    @Operation(summary = "Get all registered students number")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/registered-amount")
    public ResponseEntity<Map<String, Object>> getTotalEnabledUsers(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        int totalEnabledUsers = studentStatisticsService.countByEnabled(true);
        response.put(TOTAL, totalEnabledUsers);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get registration statistic")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/registration-data")
    public ResponseEntity<Map<String, Object>> getRegistrationData(Authentication authentication) {
        try {
            Map<String, Integer> registrationData = studentStatisticsService.getRegistrationData();
            Map<String, Object> response = new HashMap<>(registrationData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.warn(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Get total universities")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/universities")
    public ResponseEntity<Map<String, Object>> getTotalUniversities(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        int totalUniversities = universityService.countRegistered(LocalDateTime.of(2023, 9, 1,0,0));
        response.put(TOTAL, totalUniversities);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get total amount of created events")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/created-events")
    public ResponseEntity<Map<String, Object>> getTotalCreatedEvents(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        int totalCreatedEvents = eventService.getCreatedEventCount();
        response.put(TOTAL, totalCreatedEvents);
        return ResponseEntity.ok(response);
    }
}
