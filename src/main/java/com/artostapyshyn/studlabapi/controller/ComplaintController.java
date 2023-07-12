package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Complaint;
import com.artostapyshyn.studlabapi.service.ComplaintService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;

@RestController
@RequestMapping("/api/v1/complaints")
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Log4j2
public class ComplaintController {

    private final ComplaintService complaintService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllComplaints() {
        List<Complaint> complaints = complaintService.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put(CODE, "200");
        response.put(STATUS, "success");
        response.put(MESSAGE, "Complaints retrieved successfully");
        response.put("complaints", complaints);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @GetMapping("/closed")
    public ResponseEntity<Map<String, Object>> getAllClosedComplaints() {
        List<Complaint> complaints = complaintService.findClosedComplaints();
        Map<String, Object> response = new HashMap<>();
        response.put(CODE, "200");
        response.put(STATUS, "success");
        response.put(MESSAGE, "Closed complaints retrieved successfully");
        response.put("complaints", complaints);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> saveComplaint(@RequestBody Complaint complaint) {
        Map<String, Object> response = new HashMap<>();
        if (complaint == null) {
            response.put(CODE, "400");
            response.put(STATUS, "error");
            response.put(MESSAGE, "Invalid complaint");
            return ResponseEntity.badRequest().body(response);
        }

        Complaint savedComplaint = complaintService.saveComplaint(complaint);
        if (savedComplaint == null) {
            response.put(CODE, "500");
            response.put(STATUS, "error");
            response.put(MESSAGE, "Failed to save complaint");
            return ResponseEntity.internalServerError().body(response);
        }

        response.put(CODE, "200");
        response.put(STATUS, "success");
        response.put(MESSAGE, "Complaint saved successfully");
        response.put("complaint", savedComplaint);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processComplaint(@RequestBody Complaint complaint) {
        Map<String, Object> response = new HashMap<>();

        if (complaint == null) {
            return ResponseEntity.badRequest().build();
        }

        complaintService.processComplaints(complaint);
        response.put(CODE, "200");
        response.put(STATUS, "success");
        response.put(MESSAGE, "Processed successfully");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeComplaints(@RequestParam("complaintId") Long complaintId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Complaint> complaint = complaintService.findById(complaintId);
        if (complaint.isPresent()) {
            complaintService.delete(complaint.get());
            response.put(CODE, "200");
            response.put(STATUS, "success");
            response.put(MESSAGE, "Complaint deleted successfully");
            return ResponseEntity.ok().body(response);
        }
        response.put(CODE, "404");
        response.put(STATUS, "error");
        response.put(MESSAGE, "Complaint not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
