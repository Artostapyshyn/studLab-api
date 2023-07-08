package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Complaint;
import com.artostapyshyn.studlabapi.service.ComplaintService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/complaints")
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Log4j2
public class ComplaintController {

    private final ComplaintService complaintService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @GetMapping("/all")
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        List<Complaint> complaints = complaintService.findAll();
        return ResponseEntity.ok(complaints);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @GetMapping("/closed")
    public ResponseEntity<List<Complaint>> getAllClosedComplaints() {
        List<Complaint> complaints = complaintService.findClosedComplaints();
        return ResponseEntity.ok(complaints);
    }

    @PostMapping("/add")
    public ResponseEntity<Complaint> saveComplaint(@RequestBody Complaint complaint) {
        if (complaint == null) {
            return ResponseEntity.badRequest().build();
        }

        Complaint savedComplaint = complaintService.saveComplaint(complaint);
        if (savedComplaint == null) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(savedComplaint);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processComplaint(@RequestBody Complaint complaint) {
        Map<String, Object> response = new HashMap<>();

        if (complaint == null) {
            return ResponseEntity.badRequest().build();
        }

        complaintService.processComplaints(complaint);
        response.put("status", "Processed successfully");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeComplaints(@RequestParam("complaintId") Long complaintId) {
        Map<String, String> response = new HashMap<>();
        Optional<Complaint> complaint = complaintService.findById(complaintId);
        if (complaint.isPresent()) {
            complaintService.delete(complaint.get());
            response.put("status", "deleted");
            return ResponseEntity.ok().body(response);
        }
        response.put("error", "Complaint not found not found");
        return ResponseEntity.badRequest().body(response);
    }
}
