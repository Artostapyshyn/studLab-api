package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.service.FileService;
import com.artostapyshyn.studlabapi.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;

@Log4j2
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/student")
@CrossOrigin(maxAge = 3600, origins = "*")
public class StudentController {

    private final StudentService studentService;

    private final FileService fileService;

    @GetMapping(value = "/personal-info")
    public ResponseEntity<Map<String, Object>> getPersonalInfo(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        Long studentId = studentService.getAuthStudentId(authentication);
        Optional<Student> student = studentService.findById(studentId);

        if (student.isPresent()) {
            response.put("student", student.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@RequestBody @NotNull Student student) {
        Map<String, Object> response = new HashMap<>();
        Student existingStudent = studentService.findByFirstNameAndLastName(student.getFirstName(), student.getLastName());

        if (existingStudent != null) {
            response.put("student", existingStudent);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get all students")
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, String>>> getAllStudents() {
        List<Map<String, String>> studentData = studentService.findAll().stream()
                .map(student -> {
                    Map<String, String> data = new HashMap<>();
                    data.put("firstName", student.getFirstName());
                    data.put("lastName", student.getLastName());
                    return data;
                })
                .toList();

        return ResponseEntity.ok(studentData);
    }

    @Operation(summary = "Find student by id")
    @GetMapping("/find-by-id")
    public ResponseEntity<Student> getStudentById(@RequestParam("studentId") Long id) {
        Optional<Student> student = studentService.findById(id);

        return ResponseEntity.of(student);
    }


    @Operation(summary = "Upload resume to personal account")
    @PostMapping("/resumes")
    public ResponseEntity<Map<String, Object>> addResume(Authentication authentication,
                                                         @RequestParam("resume") MultipartFile file) throws IOException {
        Long studentId = studentService.getAuthStudentId(authentication);
        return fileService.addResume(studentId, file);
    }

    @Operation(summary = "Upload certificate to personal account")
    @PostMapping("/certificates")
    public ResponseEntity<Map<String, Object>> addCertificate(Authentication authentication,
                                                              @RequestParam("certificate") MultipartFile file) throws IOException {
        Long studentId = studentService.getAuthStudentId(authentication);
        return fileService.addCertificate(studentId, file);
    }

    @DeleteMapping("/remove-resume")
    public ResponseEntity<Map<String, Object>> deleteResume(Authentication authentication,
                                                            @RequestParam("fileName") String fileName) {
        Long studentId = studentService.getAuthStudentId(authentication);
        return fileService.deleteResume(studentId, fileName);
    }

    @Operation(summary = "Delete certificate from personal account")
    @DeleteMapping("/remove-certificate")
    public ResponseEntity<Map<String, Object>> deleteCertificate(Authentication authentication,
                                                                 @RequestParam("fileName") String fileName) {
        Long studentId = studentService.getAuthStudentId(authentication);
        return fileService.deleteCertificate(studentId, fileName);
    }

    @Operation(summary = "Edit student account.")
    @PutMapping("/edit")
    public ResponseEntity<Student> editStudent(@RequestBody Student student, Authentication authentication) {
        Optional<Student> optionalStudent = studentService.findById(studentService.getAuthStudentId(authentication));

        if (optionalStudent.isPresent()) {
            Student existingStudent = optionalStudent.get();
            studentService.updateStudent(existingStudent, student);
            studentService.save(existingStudent);
            return ResponseEntity.ok(existingStudent);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete student account")
    @DeleteMapping("/delete-account")
    public ResponseEntity<Map<String, Object>> deleteStudent(Authentication authentication) {
        Long studentIdToDelete = studentService.getAuthStudentId(authentication);
        studentService.deleteById(studentIdToDelete);

        Map<String, Object> response = new HashMap<>();
        response.put(MESSAGE, "Student account deleted successfully");
        return ResponseEntity.ok().body(response);
    }
}
