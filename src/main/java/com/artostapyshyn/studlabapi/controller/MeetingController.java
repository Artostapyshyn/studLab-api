package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Meeting;
import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.exception.exceptions.ResourceNotFoundException;
import com.artostapyshyn.studlabapi.service.MeetingService;
import com.artostapyshyn.studlabapi.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;

@Log4j2
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/meetings")
@CrossOrigin(maxAge = 3600, origins = "*")
public class MeetingController {

    private final MeetingService meetingService;

    private final StudentService studentService;

    @Operation(summary = "Find student created meetings")
    @GetMapping("/find-by-student-id")
    public ResponseEntity<List<Meeting>> getStudentMeetings(@RequestParam("studentId") Long studentId) {
        List<Meeting> meetings = meetingService.findAllByAuthorId(studentId);
        return ResponseEntity.ok(meetings);
    }

    @Operation(summary = "Get all meetings")
    @GetMapping("/all")
    public ResponseEntity<List<Meeting>> getAllMeetings() {
        List<Meeting> meetings = meetingService.findAll();
        return ResponseEntity.ok(meetings);
    }

    @Operation(summary = "Add an meeting.")
    @PostMapping("/add")
    public ResponseEntity<Meeting> addMeeting(@RequestBody @NotNull Meeting meeting, Authentication authentication) {
        Long studentId = studentService.getAuthStudentId(authentication);
        try {
            Student author = studentService.findById(studentId).orElseThrow(() -> new ResourceNotFoundException("Student not found"));

            meeting.setAuthor(author);
            Meeting savedMeeting = meetingService.save(meeting);
            return ResponseEntity.ok(savedMeeting);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Edit an Meeting.")
    @PutMapping("/edit")
    public ResponseEntity<Meeting> editMeeting(@RequestBody @NotNull Meeting updatedMeeting, Authentication authentication) {
        Optional<Meeting> existingMeetingOpt = meetingService.findMeetingById(updatedMeeting.getId());
        Long authorId = studentService.getAuthStudentId(authentication);

        if (existingMeetingOpt.isPresent()
                && Objects.equals(existingMeetingOpt.get().getAuthor().getId(), authorId)) {
            Meeting existingMeeting = existingMeetingOpt.get();
            meetingService.updateMeeting(existingMeeting, updatedMeeting);
            return ResponseEntity.ok(existingMeeting);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete an meeting by id.")
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteMeeting(@RequestParam("meetingId") Long meetingId, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        Optional<Meeting> existingMeeting = meetingService.findMeetingById(meetingId);

        Long authorId = studentService.getAuthStudentId(authentication);

        if (existingMeeting.isPresent()
                && Objects.equals(existingMeeting.get().getAuthor().getId(), authorId)) {
            try {
                meetingService.deleteById(meetingId);
                response.put(MESSAGE, "Meeting deleted successfully");
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                response.put(MESSAGE, "Error while deleting meeting. Please check server logs for details.");
                return ResponseEntity.internalServerError().body(response);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Participate in meeting")
    @PutMapping("/participate")
    public ResponseEntity<Map<String, Object>> participateInMeeting(@RequestParam("meetingId") Long meetingId,
                                                                    Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        if (meetingId != null) {
            try {
                Optional<Meeting> meeting = meetingService.findById(meetingId);

                if (meeting.isEmpty()) {
                    response.put(ERROR, "Meeting not found.");
                    return ResponseEntity.badRequest().body(response);
                }

                Long studentId = studentService.getAuthStudentId(authentication);
                Optional<Student> student = studentService.findById(studentId);

                if (student.isEmpty()) {
                    response.put(ERROR, "Student not found.");
                    return ResponseEntity.badRequest().body(response);
                }

                meeting.get().getParticipants().add(student.get());
                meetingService.save(meeting.get());

                response.put(MESSAGE, "Applied successfully");
                return ResponseEntity.ok(response);
            } catch (Exception ex) {
                response.put(ERROR, "Error while applying to meeting: " + ex.getMessage());
                return ResponseEntity.internalServerError().body(response);
            }
        } else {
            response.put(ERROR, "No meeting id.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Cancel Participation in meeting")
    @PutMapping("/cancel-participation")
    public ResponseEntity<Map<String, Object>> cancelParticipation(@NotNull @RequestParam("meetingId") Long meetingId,
                                                                   Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Meeting> meeting = meetingService.findById(meetingId);

            if (meeting.isEmpty()) {
                response.put(ERROR, "Meeting not found.");
                return ResponseEntity.badRequest().body(response);
            }

            Long studentId = studentService.getAuthStudentId(authentication);
            Optional<Student> student = studentService.findById(studentId);

            if (student.isEmpty()) {
                response.put(ERROR, "Student not found.");
                return ResponseEntity.badRequest().body(response);
            }

            meeting.get().getParticipants().remove(student.get());
            meetingService.save(meeting.get());

            response.put(MESSAGE, "Canceled successfully");
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            response.put(ERROR, "Error while canceling participation in meeting: " + ex.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
