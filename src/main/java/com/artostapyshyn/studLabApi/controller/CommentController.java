package com.artostapyshyn.studLabApi.controller;

import com.artostapyshyn.studLabApi.entity.Comment;
import com.artostapyshyn.studLabApi.entity.Event;
import com.artostapyshyn.studLabApi.entity.Student;
import com.artostapyshyn.studLabApi.service.CommentService;
import com.artostapyshyn.studLabApi.service.StudentService;
import com.artostapyshyn.studLabApi.service.impl.EventServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@CrossOrigin(origins = "https://stud-lab-api.onrender.com", maxAge = 3600)
@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final EventServiceImpl eventService;

    private final CommentService commentService;

    private final StudentService studentService;

    @Operation(summary = "Add comment to event")
    @PostMapping
    public ResponseEntity<?> addCommentToEvent(@RequestParam("eventId") Long eventId, @RequestBody Comment comment, Authentication authentication) {
        List<Object> response = new ArrayList<>();
        Event event = eventService.findEventById(eventId);
        if (event != null) {
            Optional<Student> student = studentService.findById(getAuthStudentId(authentication));
            if(student.isPresent()) {
                comment.setStudent(student.get());
                event.addComment(comment);
                commentService.save(comment);
                eventService.save(event);
                response.add(comment);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.add("Student not found.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } else {
            response.add("Event not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get all comments to event")
    @GetMapping
    public ResponseEntity<?> getCommentsForEvent(@RequestParam("eventId") Long eventId) {
        List<Object> response = new ArrayList<>();
        Event event = eventService.findEventById(eventId);
        if (event != null) {
            Set<Comment> comments = event.getEventComments();
            response.add(comments);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.add("Event not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    private Long getAuthStudentId(Authentication authentication) {
        String studentEmail = authentication.getName();
        Student student = studentService.findByEmail(studentEmail);
        return student.getId();
    }
}
