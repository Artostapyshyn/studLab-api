package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Complaint;
import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.service.ComplaintService;
import com.artostapyshyn.studlabapi.service.StudentService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/complaints")
@AllArgsConstructor
@Log4j2
public class ComplaintController {

    private final ComplaintService complaintService;

    private final StudentService studentService;

    @PostMapping("/add")
    public Complaint saveComplaint(@RequestBody Complaint complaint) {
        return complaintService.saveComplaint(complaint);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @GetMapping("/all")
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        List<Complaint> complaints = complaintService.findAll();
        return ResponseEntity.ok(complaints);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeComplaints(Authentication authentication, @RequestParam("complaintId") Long complaintId) {
        Map<String, String> response = new HashMap<>();
        Long studentId = getAuthStudentId(authentication);
        Optional<Complaint> complaint = complaintService.findById(complaintId);
        if (complaint.isPresent()) {
            complaintService.delete(complaint.get());
            response.put("status", "deleted");
            return ResponseEntity.ok().body(response);
        }
        response.put("error", "Complaint not found not found");
        return ResponseEntity.badRequest().body(response);
    }

    private Long getAuthStudentId(Authentication authentication) {
        String studentEmail = authentication.getName();
        Student student = studentService.findByEmail(studentEmail);
        return student.getId();
    }

}
