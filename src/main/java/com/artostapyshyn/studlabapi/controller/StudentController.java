package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.*;
import com.artostapyshyn.studlabapi.service.AlternateRegistrationStudentService;
import com.artostapyshyn.studlabapi.service.CertificateService;
import com.artostapyshyn.studlabapi.service.ResumeService;
import com.artostapyshyn.studlabapi.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
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

    private final AlternateRegistrationStudentService alternateRegistrationStudentService;

    private final ResumeService resumeService;

    private final CertificateService certificateService;

    @Operation(summary = "Get personal information",
            security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/personal-info")
    public ResponseEntity<List<Student>> getPersonalInfo(Authentication authentication) {
        Long studentId = studentService.getAuthStudentId(authentication);
        Optional<Student> student = studentService.findById(studentId);

        List<Student> studentList = student.map(Collections::singletonList).orElse(Collections.emptyList());

        return ResponseEntity.ok(studentList);
    }

    @Operation(summary = "Get personal information",
            security = @SecurityRequirement(name = "basicAuth"))
    @PostMapping(value = "/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@RequestBody Student student) {
        Map<String, Object> response = new HashMap<>();
        Student existingStudent = studentService.findByFirstNameAndLastName(student.getFirstName(), student.getLastName());

        if (existingStudent != null) {
            response.put("student", existingStudent);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get all students",
            security = @SecurityRequirement(name = "basicAuth"))
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

    @Operation(summary = "Find student by id",
            security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/find-by-id")
    public ResponseEntity<Student> getStudentById(@RequestParam("studentId") Long id) {
        Optional<Student> student = studentService.findById(id);

        return ResponseEntity.of(student);
    }

    @Operation(summary = "Find alternate registration student by code",
            security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/alternate/find-by-id")
    public ResponseEntity<AlternateRegistrationStudent> getAlternateStudentByCode(@RequestParam("code") String code) {
        Optional<AlternateRegistrationStudent> student = alternateRegistrationStudentService.findByCode(code);
        return student.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get student resumes by token",
            security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/resumes")
    public ResponseEntity<List<Resume>> getStudentResumes(@RequestParam("studentId") Long studentId) {
        if (studentId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Resume> resumes = resumeService.findByStudentId(studentId);
        return ResponseEntity.ok(resumes);
    }

    @Operation(summary = "Get student certificates by id",
            security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/certificates")
    public ResponseEntity<List<Certificate>> getStudentCertificates(@RequestParam("studentId") Long studentId) {
        if (studentId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Certificate> certificates = certificateService.findByStudentId(studentId);
        return ResponseEntity.ok(certificates);
    }

    @Operation(summary = "Upload resume to personal account",
            security = @SecurityRequirement(name = "basicAuth"))
    @PostMapping("/add/resume")
    public ResponseEntity<Resume> addResume(Authentication authentication,
                                            @RequestParam("resume") MultipartFile file) throws IOException {
        Long studentId = studentService.getAuthStudentId(authentication);
        Optional<Student> studentOptional = studentService.findById(studentId);

        if (studentOptional.isPresent() && file != null && !file.isEmpty()) {
            Student student = studentOptional.get();
            Resume resume = new Resume();
            resume.setName(file.getOriginalFilename());
            resume.setData(file.getBytes());
            resume.setStudentId(student.getId());

            resumeService.save(resume);
            return ResponseEntity.ok(resume);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Upload certificate to personal account",
            security = @SecurityRequirement(name = "basicAuth"))
    @PostMapping("/add/certificate")
    public ResponseEntity<Certificate> addCertificate(Authentication authentication,
                                                      @RequestParam("certificate") MultipartFile file) throws IOException {
        Long studentId = studentService.getAuthStudentId(authentication);
        Optional<Student> studentOptional = studentService.findById(studentId);

        if (studentOptional.isPresent() && file != null && !file.isEmpty()) {
            Student student = studentOptional.get();
            Certificate certificate = new Certificate();
            certificate.setName(file.getOriginalFilename());
            certificate.setData(file.getBytes());
            certificate.setStudentId(student.getId());

            certificateService.save(certificate);
            return ResponseEntity.ok(certificate);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Delete resume from personal account",
            security = @SecurityRequirement(name = "basicAuth"))
    @DeleteMapping("/remove-resume")
    public ResponseEntity<Map<String, Object>> deleteResume(Authentication authentication,
                                                            @RequestParam("fileName") String fileName) {
        Long studentId = studentService.getAuthStudentId(authentication);
        Resume resume = resumeService.findByName(fileName);

        if (resume != null && resume.getStudentId().equals(studentId)) {
            Map<String, Object> response = new HashMap<>();
            resumeService.deleteById(resume.getId());

            response.put(MESSAGE, "Resume deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete certificate from personal account",
            security = @SecurityRequirement(name = "basicAuth"))
    @DeleteMapping("/remove-certificate")
    public ResponseEntity<Map<String, Object>> deleteCertificate(Authentication authentication,
                                                                 @RequestParam("fileName") String fileName) {
        Long studentId = studentService.getAuthStudentId(authentication);
        Certificate certificate = certificateService.findByName(fileName);

        if (certificate != null && certificate.getStudentId().equals(studentId)) {
            Map<String, Object> response = new HashMap<>();
            certificateService.deleteById(certificate.getId());

            response.put(MESSAGE, "Certificate deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Download resumes",
            security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/download-resume")
    public ResponseEntity<byte[]> downloadResume(@RequestParam("fileName") String filename) {
        Resume resume = resumeService.findByName(filename);

        if (resume == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] fileContent = resume.getData();

        if (fileContent == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename(filename).build());

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }

    @Operation(summary = "Download certificates",
            security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/download-certificate")
    public ResponseEntity<byte[]> downloadCertificate(@RequestParam("fileName") String filename) {
        Certificate certificate = certificateService.findByName(filename);

        if (certificate == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] fileContent = certificate.getData();

        if (fileContent == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename(filename).build());

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }

    @Operation(summary = "Edit student account.",
            security = @SecurityRequirement(name = "basicAuth"))
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

    @Operation(summary = "Delete student account",
            security = @SecurityRequirement(name = "basicAuth"))
    @DeleteMapping("/delete-account")
    public ResponseEntity<Map<String, Object>> deleteStudent(Authentication authentication) {
        Long studentIdToDelete = studentService.getAuthStudentId(authentication);
        studentService.deleteById(studentIdToDelete);

        Map<String, Object> response = new HashMap<>();
        response.put(MESSAGE, "Student account deleted successfully");
        return ResponseEntity.ok().body(response);
    }
}
