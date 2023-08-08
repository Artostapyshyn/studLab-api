package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.dto.ComplaintDto;
import com.artostapyshyn.studlabapi.entity.Complaint;
import com.artostapyshyn.studlabapi.service.ComplaintService;
import com.artostapyshyn.studlabapi.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;

@Log4j2
@Validated
@RestController
@RequestMapping("/api/v1/complaints")
@AllArgsConstructor
@CrossOrigin(maxAge = 3600, origins = "*")
public class ComplaintController {

    private final ComplaintService complaintService;

    private final StudentService studentService;

    @Operation(summary = "Get all complaints")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @GetMapping("/all")
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        List<Complaint> complaints = complaintService.findAll();
        return ResponseEntity.ok(complaints);
    }

    @Operation(summary = "Get all closed complaints")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @GetMapping("/closed")
    public ResponseEntity<List<Complaint>> getAllClosedComplaints() {
        List<Complaint> complaints = complaintService.findClosedComplaints();
        return ResponseEntity.ok(complaints);
    }

    @Operation(summary = "Add a complaint")
    @PostMapping("/add")
    public ResponseEntity<Complaint> saveComplaint(@RequestBody @NotNull ComplaintDto complaintDto, Authentication authentication) {
        complaintDto.setStudentId(studentService.getAuthStudentId(authentication));
        Complaint complaint = complaintService.saveComplaint(complaintDto);
        if (complaint == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(complaint);
    }

    @Operation(summary = "Process a complaint")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processComplaint(@RequestBody @NotNull Complaint complaint, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        Complaint savedComplaint = complaintService.save(complaint);
        complaintService.processComplaints(savedComplaint);

        response.put(MESSAGE, "Processed successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Remove complaint")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeComplaints(@RequestParam("complaintId") Long complaintId, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        Optional<Complaint> complaint = complaintService.findById(complaintId);
        if (complaint.isPresent()) {
            complaintService.delete(complaint.get());
            response.put(MESSAGE, "Complaint deleted successfully");
            return ResponseEntity.ok().body(response);
        }
        response.put("error", "Complaint not found not found");
        return ResponseEntity.badRequest().body(response);
    }
}
