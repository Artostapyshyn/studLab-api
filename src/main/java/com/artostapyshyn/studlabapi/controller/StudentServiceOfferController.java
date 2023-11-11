package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.dto.StudentOfferEditDto;
import com.artostapyshyn.studlabapi.dto.StudentServiceOfferDto;
import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.entity.StudentServiceOffer;
import com.artostapyshyn.studlabapi.exception.exceptions.ResourceNotFoundException;
import com.artostapyshyn.studlabapi.service.StudentOfferService;
import com.artostapyshyn.studlabapi.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/services")
@CrossOrigin(maxAge = 3600, origins = "*")
public class StudentServiceOfferController {

    private final StudentService studentService;

    private final StudentOfferService studentOfferService;

    private final ModelMapper modelMapper;

    @Operation(summary = "Find student created services")
    @GetMapping("/find-by-student-id")
    public ResponseEntity<List<StudentServiceOfferDto>> getStudentMeetings(@NotNull @RequestParam("studentId") Long studentId) {
        List<StudentServiceOfferDto> serviceOffers = studentOfferService.findAllByProviderId(studentId);
        return ResponseEntity.ok(serviceOffers);
    }

    @Operation(summary = "Get all services")
    @GetMapping("/all")
    public ResponseEntity<List<StudentServiceOfferDto>> getAllMeetings() {
        List<StudentServiceOfferDto> serviceOffers = studentOfferService.findAll();
        return ResponseEntity.ok(serviceOffers);
    }

    @Operation(summary = "Edit service offer.")
    @PutMapping("/edit")
    public ResponseEntity<StudentServiceOfferDto> editStudent(@RequestParam("serviceId") Long serviceId, @RequestBody StudentOfferEditDto studentOfferEditDto,
                                                  Authentication authentication) {
        Student student = studentService.findById(studentService.getAuthStudentId(authentication))
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (!Objects.equals(student.getId(), studentOfferEditDto.getProvider().getId())) {
            return ResponseEntity.badRequest().build();
        }

        StudentServiceOffer studentOffer = this.studentOfferService.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        student.setLastActiveDateTime(LocalDateTime.now());
        studentOfferService.updateStudentServices(studentOffer , studentOfferEditDto);
        StudentServiceOfferDto studentServiceOfferDto = modelMapper.map(studentOffer, StudentServiceOfferDto.class);

        return ResponseEntity.ok(studentServiceOfferDto);
    }
}
