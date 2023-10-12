package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.dto.EditDto;
import com.artostapyshyn.studlabapi.dto.StudentDto;
import com.artostapyshyn.studlabapi.entity.*;
import com.artostapyshyn.studlabapi.service.AlternateRegistrationStudentService;
import com.artostapyshyn.studlabapi.service.CertificateService;
import com.artostapyshyn.studlabapi.service.ResumeService;
import com.artostapyshyn.studlabapi.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
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

    private final ModelMapper modelMapper;

    private final AlternateRegistrationStudentService alternateRegistrationStudentService;

    private final ResumeService resumeService;

    private final CertificateService certificateService;

    @Operation(summary = "Get personal information")
    @GetMapping("/personal-info")
    public ResponseEntity<List<StudentDto>> getPersonalInfo(Authentication authentication) {
        Long studentId = studentService.getAuthStudentId(authentication);
        Optional<Student> student = studentService.findById(studentId);

        List<StudentDto> studentDtoList = student.map(stud -> Collections.singletonList(convertToDto(stud)))
                .orElse(Collections.emptyList());

        return ResponseEntity.ok(studentDtoList);
    }

    @Operation(summary = "Get personal information")
    @PostMapping(value = "/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@RequestBody Student student) {
        Map<String, Object> response = new HashMap<>();
        Student existingStudent = studentService.findByFirstNameAndLastName(student.getFirstName(), student.getLastName());

        if (existingStudent != null) {
            StudentDto studentDto = convertToDto(existingStudent);
            response.put("student", studentDto);
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

    @Operation(summary = "Find alternate registration student by code")
    @GetMapping("/alternate/find-by-id")
    public ResponseEntity<AlternateRegistrationStudent> getAlternateStudentByCode(@RequestParam("code") String code) {
        Optional<AlternateRegistrationStudent> student = alternateRegistrationStudentService.findByCode(code);
        return student.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get student resumes by token")
    @GetMapping("/resumes")
    public ResponseEntity<List<Resume>> getStudentResumes(@RequestParam("studentId") Long studentId) {
        if (studentId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Resume> resumes = resumeService.findByStudentId(studentId);
        return ResponseEntity.ok(resumes);
    }

    @Operation(summary = "Get student certificates by id")
    @GetMapping("/certificates")
    public ResponseEntity<List<Certificate>> getStudentCertificates(@RequestParam("studentId") Long studentId) {
        if (studentId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Certificate> certificates = certificateService.findByStudentId(studentId);
        return ResponseEntity.ok(certificates);
    }

    @Operation(summary = "Upload resume to personal account")
    @PostMapping("/add/resume")
    public ResponseEntity<Resume> addResume(Authentication authentication,
                                            @RequestParam("resume") MultipartFile file) throws IOException {
        Long studentId = studentService.getAuthStudentId(authentication);
        Optional<Student> studentOptional = studentService.findById(studentId);

        if (studentOptional.isPresent() && file != null && !file.isEmpty()) {
            Student student = studentOptional.get();
            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = originalFilename;

            int fileCounter = 1;
            while (resumeService.existsByStudentIdAndName(student.getId(), uniqueFilename)) {
                String fileNameWithoutExtension = FilenameUtils.removeExtension(originalFilename);
                String fileExtension = FilenameUtils.getExtension(originalFilename);
                uniqueFilename = fileNameWithoutExtension + "_" + fileCounter + "." + fileExtension;
                fileCounter++;
            }

            Resume resume = new Resume();
            resume.setName(uniqueFilename);
            resume.setData(file.getBytes());
            resume.setStudentId(student.getId());

            resumeService.save(resume);
            return ResponseEntity.ok(resume);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Upload certificate to personal account")
    @PostMapping("/add/certificate")
    public ResponseEntity<Certificate> addCertificate(Authentication authentication,
                                                      @RequestParam("certificate") MultipartFile file) throws IOException {
        Long studentId = studentService.getAuthStudentId(authentication);
        Optional<Student> studentOptional = studentService.findById(studentId);

        if (studentOptional.isPresent() && file != null && !file.isEmpty()) {
            Student student = studentOptional.get();
            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = originalFilename;

            int fileCounter = 1;
            while (certificateService.existsByStudentIdAndName(student.getId(), uniqueFilename)) {
                String fileNameWithoutExtension = FilenameUtils.removeExtension(originalFilename);
                String fileExtension = FilenameUtils.getExtension(originalFilename);
                uniqueFilename = fileNameWithoutExtension + "_" + fileCounter + "." + fileExtension;
                fileCounter++;
            }

            Certificate certificate = new Certificate();
            certificate.setName(uniqueFilename);
            certificate.setData(file.getBytes());
            certificate.setStudentId(student.getId());

            certificateService.save(certificate);
            return ResponseEntity.ok(certificate);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Delete resume from personal account")
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

    @Operation(summary = "Delete certificate from personal account")
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

    @Operation(summary = "Download resumes")
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
        String contentType;

        String fileExtension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        contentType = switch (fileExtension) {
            case "pdf" -> "application/pdf";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default -> "application/octet-stream";
        };

        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename(filename).build());

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }

    @Operation(summary = "Download certificates")
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
        String contentType;

        String fileExtension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        contentType = switch (fileExtension) {
            case "jpeg", "jpg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            default -> "application/octet-stream";
        };

        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename(filename).build());

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }


    @Operation(summary = "Edit student account.")
    @PutMapping("/edit")
    public ResponseEntity<StudentDto> editStudent(@RequestBody EditDto student, Authentication authentication) {
        Optional<Student> optionalStudent = studentService.findById(studentService.getAuthStudentId(authentication));

        if (optionalStudent.isPresent()) {
            Student existingStudent = optionalStudent.get();
            studentService.updateStudent(existingStudent, student);
            studentService.save(existingStudent);
            StudentDto studentDto = modelMapper.map(existingStudent, StudentDto.class);

            return ResponseEntity.ok(studentDto);
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

    public StudentDto convertToDto(Student student) {
        return modelMapper.map(student, StudentDto.class);
    }

}
