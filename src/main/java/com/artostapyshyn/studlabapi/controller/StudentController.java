package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.service.FileService;
import com.artostapyshyn.studlabapi.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;

@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/student")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;

    private final FileService fileService;

    @Operation(summary = "Get personal information")
    @GetMapping("/personal-info")
    public ResponseEntity<Map<String, Object>> getPersonalInfo(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        Long studentId = studentService.getAuthStudentId(authentication);
        Optional<Student> student = studentService.findById(studentId);

        if (student.isPresent()) {
            response.put(CODE, "200");
            response.put(STATUS, "success");
            response.put(MESSAGE, "Personal information retrieved successfully");
            response.put("student", student.get());
            return ResponseEntity.ok(response);
        } else {
            response.put(CODE, "404");
            response.put(STATUS, "error");
            response.put(MESSAGE, "Student not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "Find student by id")
    @GetMapping("/find-by-id")
    public ResponseEntity<Map<String, Object>> getStudentById(@RequestParam("studentId") Long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Student> student = studentService.findById(id);

        if (student.isPresent()) {
            response.put(CODE, "200");
            response.put(STATUS, "success");
            response.put(MESSAGE, "Student found");
            response.put("student", student.get());
            return ResponseEntity.ok(response);
        } else {
            response.put(CODE, "404");
            response.put(STATUS, "error");
            response.put(MESSAGE, "Student not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "Get personal information")
    @PostMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@RequestBody Student student) {
        Map<String, Object> response = new HashMap<>();
        Student existingStudent = studentService.findByFirstNameAndLastName(student.getFirstName(), student.getLastName());

        if (existingStudent != null) {
            response.put(CODE, "200");
            response.put(STATUS, "success");
            response.put(MESSAGE, "Personal information retrieved successfully");
            response.put("student", existingStudent);
            return ResponseEntity.ok(response);
        } else {
            response.put(CODE, "404");
            response.put(STATUS, "error");
            response.put(MESSAGE, "Student not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "Get all students")
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllStudents() {
        List<Map<String, String>> studentData = new ArrayList<>();
        List<Student> students = studentService.findAll();

        for (Student student : students) {
            Map<String, String> data = new HashMap<>();
            data.put("firstName", student.getFirstName());
            data.put("lastName", student.getLastName());
            studentData.add(data);
        }

        Map<String, Object> response = new HashMap<>();
        response.put(CODE, "200");
        response.put(STATUS, "success");
        response.put(MESSAGE, "All students retrieved successfully");
        response.put("students", studentData);

        return ResponseEntity.ok(response);
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
    public ResponseEntity<Map<String, Object>> editEvent(@RequestBody Student student, Authentication authentication) {
        Optional<Student> optionalStudent = studentService.findById(studentService.getAuthStudentId(authentication));

        if (optionalStudent.isPresent()) {
            Student existingStudent = optionalStudent.get();
            studentService.updateStudent(existingStudent, student);
            studentService.save(existingStudent);

            Map<String, Object> response = new HashMap<>();
            response.put(CODE, "200");
            response.put(STATUS, "success");
            response.put(MESSAGE, "Student account updated successfully");
            response.put("student", existingStudent);
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put(CODE, "404");
            response.put(STATUS, "error");
            response.put(MESSAGE, "Student not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "Delete student account")
    @DeleteMapping("/delete-account")
    public ResponseEntity<Map<String, Object>> deleteStudent(Authentication authentication) {
        Long studentIdToDelete = studentService.getAuthStudentId(authentication);
        studentService.deleteById(studentIdToDelete);

        Map<String, Object> response = new HashMap<>();
        response.put(CODE, "200");
        response.put(STATUS, "success");
        response.put(MESSAGE, "Student account deleted successfully");

        return ResponseEntity.ok().body(response);
    }
}
