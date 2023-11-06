package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.dto.MeetingDto;
import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.entity.Meeting;
import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.exception.exceptions.ResourceNotFoundException;
import com.artostapyshyn.studlabapi.service.EventService;
import com.artostapyshyn.studlabapi.service.MeetingService;
import com.artostapyshyn.studlabapi.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;
import static com.artostapyshyn.studlabapi.enums.MeetingType.EVENT_BASED;

@Log4j2
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/meetings")
@CrossOrigin(maxAge = 3600, origins = "*")
public class MeetingController {

    private final MeetingService meetingService;

    private final StudentService studentService;

    private final EventService eventService;

    private final ModelMapper modelMapper;

    @Operation(summary = "Find student created meetings")
    @GetMapping("/find-by-student-id")
    public ResponseEntity<List<MeetingDto>> getStudentMeetings(@NotNull @RequestParam("studentId") Long studentId) {
        List<MeetingDto> meetings = meetingService.findAllByAuthorId(studentId);
        return ResponseEntity.ok(meetings);
    }

    @Operation(summary = "Get all meetings")
    @GetMapping("/all")
    public ResponseEntity<List<MeetingDto>> getAllMeetings() {
        List<MeetingDto> meetings = meetingService.findAll();
        return ResponseEntity.ok(meetings);
    }

    @Operation(summary = "Add an meeting.")
    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addMeeting(@RequestBody @NotNull MeetingDto meetingDTO, Authentication authentication) {
        try {
            Long studentId = studentService.getAuthStudentId(authentication);
            Student student = studentService.findById(studentId).orElseThrow();
            student.setLastActiveDateTime(LocalDateTime.now());
            if(studentId == null) {
                return ResponseEntity.badRequest().body(Collections.singletonMap(ERROR, "Invalid student."));
            }

            Student author = studentService.findById(studentId).orElseThrow(() -> new ResourceNotFoundException("Student not found"));
            if(meetingDTO.getMeetingType().equals(EVENT_BASED)) {
                Event event = eventService.findByNameOfEvent(meetingDTO.getEventName());
                if(event == null){
                    return ResponseEntity.badRequest().body(Collections.singletonMap(ERROR, "Event not found"));
                }
                meetingDTO.setEventName(event.getNameOfEvent());
            }

            Meeting meeting = convertToEntity(meetingDTO);
            meeting.setAuthor(author);
            meetingService.save(meeting);

            return ResponseEntity.ok(Collections.singletonMap(MESSAGE, "Meeting created successfully."));
        } catch (Exception e) {
            log.error("Error creating meeting", e);
            return ResponseEntity.internalServerError().body(Collections.singletonMap(ERROR, e.getMessage()));
        }
    }

    private Meeting convertToEntity(MeetingDto meetingDTO) {
       modelMapper.getConfiguration().setSkipNullEnabled(true);
       return modelMapper.map(meetingDTO, Meeting.class);
    }

    @Operation(summary = "Edit an Meeting.")
    @PutMapping("/edit")
    public ResponseEntity<Meeting> editMeeting(@RequestBody @NotNull Meeting updatedMeeting, Authentication authentication) {
        Optional<Meeting> existingMeetingOpt = meetingService.findMeetingById(updatedMeeting.getId());
        Long authorId = studentService.getAuthStudentId(authentication);

        Student student = studentService.findById(authorId ).orElseThrow();
        student.setLastActiveDateTime(LocalDateTime.now());

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
    public ResponseEntity<Map<String, Object>> deleteMeeting(@NotNull @RequestParam("meetingId") Long meetingId, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        Optional<Meeting> existingMeeting = meetingService.findMeetingById(meetingId);

        Long authorId = studentService.getAuthStudentId(authentication);
        Student student = studentService.findById(authorId ).orElseThrow();
        student.setLastActiveDateTime(LocalDateTime.now());

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
    public ResponseEntity<Map<String, Object>> participateInMeeting(@NotNull @RequestParam("meetingId") Long meetingId,
                                                                    Authentication authentication) {
        return processMeetingParticipation(meetingId, authentication, true);
    }

    @Operation(summary = "Cancel Participation in meeting")
    @PutMapping("/cancel-participation")
    public ResponseEntity<Map<String, Object>> cancelParticipation(@NotNull @RequestParam("meetingId") Long meetingId,
                                                                   Authentication authentication) {
        return processMeetingParticipation(meetingId, authentication, false);
    }

    private ResponseEntity<Map<String, Object>> processMeetingParticipation(Long meetingId, Authentication authentication, boolean isParticipate) {
        Map<String, Object> response = new HashMap<>();
        if (meetingId == null) {
            response.put(ERROR, "No meeting id.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Optional<Meeting> meeting = meetingService.findById(meetingId);
            if (meeting.isEmpty()) {
                response.put(ERROR, "Meeting not found.");
                return ResponseEntity.badRequest().body(response);
            }

            Long studentId = studentService.getAuthStudentId(authentication);
            Optional<Student> student = studentService.findById(studentId);
            student.get().setLastActiveDateTime(LocalDateTime.now());

            if (isParticipate) {
                meeting.get().getParticipants().add(student.get());
                response.put(MESSAGE, "Applied successfully");
            } else {
                meeting.get().getParticipants().remove(student.get());
                response.put(MESSAGE, "Canceled successfully");
            }

            meetingService.save(meeting.get());
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            response.put(ERROR, "Error while " + (isParticipate ? "applying to" : "canceling participation in") + " meeting: " + ex.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Operation(summary = "Get student participated meetings")
    @GetMapping("/get")
    public ResponseEntity<List<MeetingDto>> getGetParticipatedByStudentId(Authentication authentication) {
        Long studentId = studentService.getAuthStudentId(authentication);
        Student student = studentService.findById(studentId).orElseThrow();
        student.setLastActiveDateTime(LocalDateTime.now());
        return getListResponseEntity(studentId);
    }

    private ResponseEntity<List<MeetingDto>> getListResponseEntity(Long studentId) {
        if (studentId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Meeting> meetings = meetingService.findAllByParticipantsId(studentId);
        List<MeetingDto> favMeetings = meetings.stream()
                .map(meeting -> modelMapper.map(meeting, MeetingDto.class))
                .toList();
        return ResponseEntity.ok(favMeetings);
    }
}
