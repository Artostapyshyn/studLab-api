package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Comment;
import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.entity.Reply;
import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.service.CommentService;
import com.artostapyshyn.studlabapi.service.EventService;
import com.artostapyshyn.studlabapi.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(maxAge = 3600)
@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final EventService eventService;

    private final CommentService commentService;

    private final StudentService studentService;

    @Operation(summary = "Add comment to event")
    @PostMapping("/add")
    public ResponseEntity<?> addCommentToEvent(@RequestParam("eventId") Long eventId, @RequestBody Comment comment, Authentication authentication) {
        List<Object> response = new ArrayList<>();
        Optional<Event> event = eventService.findEventById(eventId);
        if (event.isPresent()) {
            Optional<Student> optionalStudent = studentService.findById(getAuthStudentId(authentication));
            if(optionalStudent.isPresent()) {
                Student student = optionalStudent.get();
                comment.setStudent(student);
                event.get().addComment(comment);
                commentService.save(comment);
                eventService.save(event.get());
                response.add(comment);
                return ResponseEntity.ok().body(response);
            } else {
                response.add("Student not found.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } else {
            response.add("Event not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Reply to comment")
    @PostMapping("/reply")
    public ResponseEntity<?> addReplyToComment(@RequestBody Reply reply, @RequestParam("commentId") Long commentId) {
        Map<String, Object> responseMap = new HashMap<>();
        commentService.addReplyToComment(reply, commentId);
        responseMap.put("replied", true);
        return ResponseEntity.ok(responseMap);
    }

    @Operation(summary = "Get all comments to event")
    @GetMapping("/all")
    public ResponseEntity<?> getCommentsForEvent(@RequestParam("eventId") Long eventId, Authentication authentication) {
        Optional<Event> optionalEvent = eventService.findEventById(eventId);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            Set<Comment> comments = event.getEventComments();
            List<Comment> commentList = new ArrayList<>(comments);
            return ResponseEntity.ok(commentList);
        } else {
            String errorMessage = "Event not found.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }

    private Long getAuthStudentId(Authentication authentication) {
        String studentEmail = authentication.getName();
        Student student = studentService.findByEmail(studentEmail);
        return student.getId();
    }
}
