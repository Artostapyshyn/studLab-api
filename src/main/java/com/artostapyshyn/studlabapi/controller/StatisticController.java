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

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;

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
        response.put(CODE, "200");
        response.put(STATUS, "error");
        response.put(MESSAGE, "Total enabled users retrieved successfully");
        response.put(TOTAL, totalEnabledUsers);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/registration-data")
    public ResponseEntity<Map<String, Object>> getRegistrationData() {
        try {
            Map<String, Integer> registrationData = studentService.getRegistrationData();

            Map<String, Object> response = new HashMap<>();
            response.put(CODE, "200");
            response.put(STATUS, SUCCESS);
            response.put(MESSAGE, "Registration data retrieved successfully");
            response.putAll(registrationData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
                throw new RuntimeException("Error retrieving registration data");
        }
    }

    @GetMapping("/universities")
    public ResponseEntity<Map<String, Object>> getTotalUniversities() {
        Map<String, Object> response = new HashMap<>();
        int totalUniversities = universityService.findAll().size();
        response.put(CODE, "200");
        response.put(STATUS, SUCCESS);
        response.put(MESSAGE, "Total universities retrieved successfully");
        response.put(TOTAL, totalUniversities);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/created-events")
    public ResponseEntity<Map<String, Object>> getTotalCreatedEvents() {
        Map<String, Object> response = new HashMap<>();
        int totalCreatedEvents = eventService.getCreatedEventCount();
        response.put(CODE, "200");
        response.put(STATUS, SUCCESS);
        response.put(MESSAGE, "Total created events retrieved successfully");
        response.put(TOTAL, totalCreatedEvents);
        return ResponseEntity.ok(response);
    }
}
