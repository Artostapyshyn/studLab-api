package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/student")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "Get personal information")
    @GetMapping("/personal-info")
    public ResponseEntity<List<Object>> getPersonalInfo(Authentication authentication) {
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

    @Operation(summary = "Find student by id")
    @GetMapping("/find-by-id")
    public ResponseEntity<Student> getStudentById(@RequestParam("studentId") Long id) {
        List<Object> response = new ArrayList<>();
        Optional<Student> student = studentService.findById(id);
        response.add(student);
        return student.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get personal information")
    @PostMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestBody Student student) {
        List<Object> response = new ArrayList<>();
        Student existingStudent = studentService.findByFirstNameAndLastName(student.getFirstName(), student.getLastName());

        if (existingStudent != null) {
            response.add(existingStudent);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.add("Student not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get all students")
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, String>>> getAllStudents() {
        List<Map<String, String>> studentData = new ArrayList<>();
        List<Student> students = studentService.findAll();
        for (Student student : students) {
            Map<String, String> data = new HashMap<>();
            data.put("firstName", student.getFirstName());
            data.put("lastName", student.getLastName());
            studentData.add(data);
        }

        return ResponseEntity.ok(studentData);
    }

    @Operation(summary = "Uplodad resume to personal account")
    @PostMapping("/resumes")
    public ResponseEntity<Map<String, Object>> addResume(Authentication authentication, @RequestParam("resume") MultipartFile file) throws IOException {
        Map<String, Object> response = new HashMap<>();
        Long studentId = getAuthStudentId(authentication);
        Optional<Student> studentOptional = studentService.findById(studentId);
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            byte[] resumeBytes = file.getBytes();
            String resumeBase64 = Base64.getEncoder().encodeToString(resumeBytes);

            Set<String> resumes = student.getResumes();
            resumes.add(resumeBase64);
            student.setResumes(resumes);

            Set<String> resumeFilenames = student.getResumeFilenames();
            resumeFilenames.add(file.getOriginalFilename());
            student.setResumeFilenames(resumeFilenames);

            studentService.save(student);
            response.put("message", "Resume added");
            response.put("fileName", file.getOriginalFilename());
            response.put("resumeBase64", resumeBase64);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Upload certificate to personal account")
    @PostMapping("/certificates")
    public ResponseEntity<Map<String, Object>> addCertificate(Authentication authentication, @RequestParam("certificate") MultipartFile file) throws IOException {
        Map<String, Object> responseMap = new HashMap<>();
        Long studentId = getAuthStudentId(authentication);
        Optional<Student> studentOptional = studentService.findById(studentId);
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            byte[] certificateBytes = file.getBytes();
            String certificateBase64 = Base64.getEncoder().encodeToString(certificateBytes);

            Set<String> certificates = student.getCertificates();
            certificates.add(certificateBase64);
            student.setCertificates(certificates);

            Set<String> certificatesFilenames = student.getCertificatesFilenames();
            certificatesFilenames.add(file.getOriginalFilename());
            student.setCertificatesFilenames(certificatesFilenames);

            studentService.save(student);
            responseMap.put("message", "Certificate added");
            responseMap.put("fileName", file.getOriginalFilename());
            responseMap.put("resumeBase64", certificateBase64);
            return ResponseEntity.ok(responseMap);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete resume from personal account")
    @DeleteMapping("/remove-resume")
    public ResponseEntity<Map<String, Object>> deleteResume(Authentication authentication, @RequestParam("fileName") String fileName) {
        Map<String, Object> response = new HashMap<>();
        Long studentId = getAuthStudentId(authentication);
        Optional<Student> studentOptional = studentService.findById(studentId);
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            Set<String> resumes = student.getResumes();
            Set<String> resumeFilenames = student.getResumeFilenames();

            boolean removed = false;
            Iterator<String> resumeIterator = resumes.iterator();
            Iterator<String> filenameIterator = resumeFilenames.iterator();
            while (resumeIterator.hasNext() && filenameIterator.hasNext()) {
                String resume = resumeIterator.next();
                String filename = filenameIterator.next();
                if (filename.equals(fileName)) {
                    resumeIterator.remove();
                    filenameIterator.remove();
                    removed = true;
                    break;
                }
            }

            if (removed) {
                student.setResumes(resumes);
                student.setResumeFilenames(resumeFilenames);
                studentService.save(student);
                response.put("message", "Resume deleted");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Resume not found");
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete certificate from personal account")
    @DeleteMapping("/remove-certificate")
    public ResponseEntity<Map<String, Object>> deleteCertificate(Authentication authentication, @RequestParam("fileName") String fileName) {
        Map<String, Object> response = new HashMap<>();
        Long studentId = getAuthStudentId(authentication);
        Optional<Student> studentOptional = studentService.findById(studentId);
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            Set<String> certificates = student.getCertificates();
            Set<String> certificateFilenames = student.getCertificatesFilenames();

            boolean removed = false;
            Iterator<String> certificateIterator = certificates.iterator();
            Iterator<String> filenameIterator = certificateFilenames.iterator();
            while (certificateIterator.hasNext() && filenameIterator.hasNext()) {
                String certificate = certificateIterator.next();
                String filename = filenameIterator.next();
                if (filename.equals(fileName)) {
                    certificateIterator.remove();
                    filenameIterator.remove();
                    removed = true;
                    break;
                }
            }

            if (removed) {
                student.setCertificates(certificates);
                student.setCertificatesFilenames(certificateFilenames);
                studentService.save(student);
                response.put("message", "Certificate deleted");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Certificate not found");
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
    public ResponseEntity<Student> editEvent(@RequestBody Student student, Authentication authentication) {
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
        if (updatedStudent.getPassword() != null) {
            String hashedPassword = new BCryptPasswordEncoder().encode(updatedStudent.getPassword());
            existingStudent.setPassword(hashedPassword);
        }
        Optional.ofNullable(updatedStudent.getBirthDate()).ifPresent(existingStudent::setBirthDate);
        Optional.ofNullable(updatedStudent.getMajor()).ifPresent(existingStudent::setMajor);
        Optional.ofNullable(updatedStudent.getCourse()).ifPresent(existingStudent::setCourse);
        Optional.ofNullable(updatedStudent.getPhotoBytes()).ifPresent(existingStudent::setPhotoBytes);
    }

    @Operation(summary = "Delete student account")
    @DeleteMapping("/delete-account")
    public ResponseEntity<Void> deleteStudent(Authentication authentication) {
        Long studentIdToDelete = getAuthStudentId(authentication);
        studentService.deleteById(studentIdToDelete);
        return ResponseEntity.noContent().build();
    }
}
