package com.artostapyshyn.studLabApi.controller;

import com.artostapyshyn.studLabApi.entity.Student;
import com.artostapyshyn.studLabApi.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@CrossOrigin(origins = "https://stud-lab-api.onrender.com", maxAge = 3600)
@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/students")
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "Uplodad resume to personal account")
    @PostMapping("/resumes")
    public ResponseEntity<?> addResume(Authentication authentication, @RequestParam("file") MultipartFile file) throws IOException {
        Long studentId = getAuthStudentId(authentication);
        Optional<Student> student = studentService.findById(studentId);
        if (student.isPresent()) {
            byte[] resumeBytes = file.getBytes();
            Set<byte[]> resumes = student.get().getResumes();
            resumes.add(resumeBytes);
            student.get().setResumes(resumes);

            studentService.save(student.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Uplodad certificate to personal account")
    @PostMapping("/certificates")
    public ResponseEntity<?> addCertificate(Authentication authentication, @RequestParam("file") MultipartFile file) throws IOException {
        Long studentId = getAuthStudentId(authentication);
        Optional<Student> student = studentService.findById(studentId);
        if (student.isPresent()) {
            byte[] certificateBytes = file.getBytes();
            Set<byte[]> certificates = student.get().getCertificates();
            certificates.add(certificateBytes);
            student.get().setCertificates(certificates);

            studentService.save(student.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private Long getAuthStudentId(Authentication authentication) {
        String studentEmail = authentication.getName();
        Student student = studentService.findByEmail(studentEmail);
        return student.getId();
    }
}
