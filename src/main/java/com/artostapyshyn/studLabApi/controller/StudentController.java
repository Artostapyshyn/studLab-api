package com.artostapyshyn.studLabApi.controller;

import com.artostapyshyn.studLabApi.entity.Student;
import com.artostapyshyn.studLabApi.service.StudentService;
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

@CrossOrigin(maxAge = 3600)
@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/student")
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "Get personal information")
    @GetMapping("/personal-info")
    public ResponseEntity<?> getPersonalInfo(Authentication authentication) {
        List<Object> response = new ArrayList<>();
        Long studentId = getAuthStudentId(authentication);
        Optional<Student> student = studentService.findById(studentId);
        if (student.isPresent()) {
            response.add(student);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.add("Student not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get all students")
    @GetMapping("/all")
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.findAll();
        log.info("Listing all students");
        return ResponseEntity.ok(students);
    }

    @Operation(summary = "Uplodad resume to personal account")
    @PostMapping("/resumes")
    public ResponseEntity<?> addResume(Authentication authentication, @RequestParam("resume") MultipartFile file) throws IOException {
        Map<String, Object> response = new HashMap<>();
        Long studentId = getAuthStudentId(authentication);
        Optional<Student> student = studentService.findById(studentId);
        if (student.isPresent()) {
            byte[] resumeBytes = file.getBytes();
            Set<byte[]> resumes = student.get().getResumes();
            resumes.add(resumeBytes);
            student.get().setResumes(resumes);

            Set<String> resumeFilenames = student.get().getResumeFilenames();
            resumeFilenames.add(file.getOriginalFilename());
            student.get().setResumeFilenames(resumeFilenames);

            studentService.save(student.get());
            response.put("resume added", true);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Uplodad certificate to personal account")
    @PostMapping("/certificates")
    public ResponseEntity<?> addCertificate(Authentication authentication, @RequestParam("certificate") MultipartFile file) throws IOException {
        Map<String, Object> responseMap = new HashMap<>();
        Long studentId = getAuthStudentId(authentication);
        Optional<Student> student = studentService.findById(studentId);
        if (student.isPresent()) {
            byte[] certificateBytes = file.getBytes();
            Set<byte[]> certificates = student.get().getCertificates();
            certificates.add(certificateBytes);
            student.get().setCertificates(certificates);

            Set<String> certificatesFilenames = student.get().getCertificatesFilenames();
            certificatesFilenames.add(file.getOriginalFilename());
            student.get().setCertificatesFilenames(certificatesFilenames);

            studentService.save(student.get());
            responseMap.put("certificate added", true);
            return ResponseEntity.ok(responseMap);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Download file from personal account")
    @GetMapping("/downloadFile")
    public ResponseEntity<?> downloadFile(Authentication authentication, @RequestParam("fileName") String filename) {
        Long studentId = getAuthStudentId(authentication);
        Optional<Student> student = studentService.findById(studentId);
        if (student.isPresent()) {
            byte[] fileBytes = null;
            Set<byte[]> files = null;
            Set<String> fileNames = null;

            if (student.get().getResumeFilenames().contains(filename)) {
                files = student.get().getResumes();
                fileNames = student.get().getResumeFilenames();
            } else if (student.get().getCertificatesFilenames().contains(filename)) {
                files = student.get().getCertificates();
                fileNames = student.get().getCertificatesFilenames();
            }

            if (files != null && fileNames != null) {
                Iterator<byte[]> iterator = files.iterator();
                Iterator<String> nameIterator = fileNames.iterator();
                while (iterator.hasNext() && nameIterator.hasNext()) {
                    byte[] file = iterator.next();
                    String name = nameIterator.next();
                    if (name.equals(filename)) {
                        fileBytes = file;
                        break;
                    }
                }
            }

            if (fileBytes != null) {
                return ResponseEntity.ok().body(Base64.getEncoder().encodeToString(fileBytes));
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private Long getAuthStudentId(Authentication authentication) {
        String studentEmail = authentication.getName();
        Student student = studentService.findByEmail(studentEmail);
        return student.getId();
    }

    @Operation(summary = "Edit student account.")
    @PutMapping("/edit")
    public ResponseEntity<?> editEvent(@RequestBody Student student, Authentication authentication) {
        Optional<Student> optionalStudent = studentService.findById(getAuthStudentId(authentication));

        if (optionalStudent.isPresent()) {
            Student existingStudent = optionalStudent.get();
            updateStudent(existingStudent, student);
            studentService.save(existingStudent);
            return ResponseEntity.ok(existingStudent);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private void updateStudent(Student existingStudent, Student updatedStudent) {
        Optional.ofNullable(updatedStudent.getFirstName()).ifPresent(existingStudent::setFirstName);
        Optional.ofNullable(updatedStudent.getLastName()).ifPresent(existingStudent::setLastName);
        Optional.ofNullable(updatedStudent.getEmail()).ifPresent(existingStudent::setEmail);
        Optional.ofNullable(updatedStudent.getPassword()).ifPresent(existingStudent::setPassword);
        Optional.ofNullable(updatedStudent.getBirthDate()).ifPresent(existingStudent::setBirthDate);
        Optional.ofNullable(updatedStudent.getMajor()).ifPresent(existingStudent::setMajor);
        Optional.ofNullable(updatedStudent.getCourse()).ifPresent(existingStudent::setCourse);
        Optional.ofNullable(updatedStudent.getPhotoBytes()).ifPresent(existingStudent::setPhotoBytes);
        Optional.ofNullable(updatedStudent.getPhotoFilename()).ifPresent(existingStudent::setPhotoFilename);
    }

    @Operation(summary = "Delete student account")
    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteStudent(Authentication authentication) {
        Long studentIdToDelete = getAuthStudentId(authentication);
        studentService.deleteById(studentIdToDelete);
        return ResponseEntity.noContent().build();
    }

}
