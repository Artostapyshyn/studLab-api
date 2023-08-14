package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.AlternateRegistrationStudent;
import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.enums.Role;
import com.artostapyshyn.studlabapi.service.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Validated
@RestController
@RequestMapping("/api/v1/auth/alternative")
@CrossOrigin(maxAge = 3600, origins = "*")
@AllArgsConstructor
public class AlternativeAuthController {

    private final StudentService studentService;

    private final AlternateRegistrationStudentService alternateRegistrationStudentService;

    private final ModelMapper modelMapper;

    @Operation(summary = "Verify alternative registration student")
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> registerAlternate(@RequestBody AlternateRegistrationStudent alternateStudent) {
        String code = alternateStudent.getCode();
        Map<String, Object> response = new HashMap<>();

        if (alternateRegistrationStudentService.isValidCode(code)) {
            Student student = modelMapper.map(alternateStudent, Student.class);
            student.setRole(Role.ROLE_STUDENT);
            studentService.save(student);

            response.put("message", "Student validated successfully");
            return ResponseEntity.ok().body(response);
        } else {
            response.put("message", "Invalid registration code.");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
