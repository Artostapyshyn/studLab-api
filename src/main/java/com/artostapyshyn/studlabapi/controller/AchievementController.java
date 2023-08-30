package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Achievement;
import com.artostapyshyn.studlabapi.service.AchievementService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@Validated
@RestController
@RequestMapping("/api/v1/achievements")
@CrossOrigin(maxAge = 3600, origins = "*")
@AllArgsConstructor
public class AchievementController {

    private AchievementService achievementService;

    @GetMapping("/all")
    public ResponseEntity<List<Achievement>> getAllAchievementsByStudentId(@RequestParam("studentId") Long studentId) {
        if (studentId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Achievement> achievements = achievementService.findByStudentId(studentId);
        return ResponseEntity.ok(achievements);
    }
}
