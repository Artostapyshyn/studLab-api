package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Comment;
import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.entity.Reply;
import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.exception.exceptions.ResourceNotFoundException;
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

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;

@Log4j2
@RestController
@RequestMapping("/api/v1/comments")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class CommentController {

    private final EventService eventService;

    private final CommentService commentService;

    private final StudentService studentService;

    @Operation(summary = "Add comment to event")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addCommentToEvent(@RequestParam("eventId") Long eventId,
                                                                 @RequestBody Comment comment,
                                                                 Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        Optional<Event> event = eventService.findEventById(eventId);
        if (event.isPresent()) {
            Optional<Student> optionalStudent = studentService.findById(studentService.getAuthStudentId(authentication));
            if (optionalStudent.isPresent()) {
                Student student = optionalStudent.get();
                comment.setStudent(student);
                event.get().addComment(comment);
                commentService.save(comment);
                eventService.save(event.get());

                response.put(CODE, "200");
                response.put(STATUS, "success");
                response.put(MESSAGE, "Comment added successfully");
                response.put("comment", comment);

                return ResponseEntity.ok(response);
            } else {
                response.put(CODE, "404");
                response.put(STATUS, "error");
                response.put(MESSAGE, "Student not found");

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } else {
            response.put(CODE, "404");
            response.put(STATUS, "error");
            response.put(MESSAGE, "Event not found");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "Reply to comment")
    @PostMapping("/reply")
    public ResponseEntity<Map<String, Object>> addReplyToComment(@RequestBody Reply reply,
                                                                 @RequestParam("commentId") Long commentId) {
        Map<String, Object> responseMap = new HashMap<>();
        commentService.addReplyToComment(reply, commentId);
        responseMap.put(CODE, "200");
        responseMap.put(STATUS, "success");
        responseMap.put(MESSAGE, "Replied successfully");
        return ResponseEntity.ok(responseMap);
    }

    @Operation(summary = "Get all comments to event")
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getCommentsForEvent(@RequestParam("eventId") Long eventId) {
        Optional<Event> optionalEvent = eventService.findEventById(eventId);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            Set<Comment> comments = event.getEventComments();
            List<Comment> commentList = new ArrayList<>(comments);

            Map<String, Object> response = new HashMap<>();
            response.put(CODE, "200");
            response.put(STATUS, "success");
            response.put(MESSAGE, "Comments retrieved successfully");
            response.put("comments", commentList);

            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put(CODE, "404");
            response.put(STATUS, "error");
            response.put(MESSAGE, "Event not found");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/like-comment")
    public ResponseEntity<Map<String, Object>> likeComment(@RequestParam("commentId") Long commentId,
                                                           Authentication authentication) {
        Map<String, Object> responseMap = new HashMap<>();
        Optional<Comment> optionalComment = commentService.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            Long currentUserId = studentService.getAuthStudentId(authentication);
            if (comment.getLikedBy().stream().anyMatch(student -> student.getId().equals(currentUserId))) {
                responseMap.put(CODE, "400");
                responseMap.put(STATUS, "error");
                responseMap.put(MESSAGE, "Comment already liked by the user");
                return ResponseEntity.badRequest().body(responseMap);
            }

            comment.setLikes(comment.getLikes() + 1);
            Student currentUser = studentService.findById(currentUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

            comment.getLikedBy().add(currentUser);
            commentService.save(comment);
            responseMap.put(CODE, "200");
            responseMap.put(STATUS, "success");
            responseMap.put(MESSAGE, "Liked successfully");
            return ResponseEntity.ok().body(responseMap);
        }
        responseMap.put(CODE, "400");
        responseMap.put(STATUS, "error");
        responseMap.put(MESSAGE, "Comment not found");
        return ResponseEntity.badRequest().body(responseMap);
    }

    @PostMapping("/unlike-comment")
    public ResponseEntity<Map<String, Object>> unlikeComment(@RequestParam("commentId") Long commentId,
                                                             Authentication authentication) {
        Map<String, Object> responseMap = new HashMap<>();
        Optional<Comment> optionalComment = commentService.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            Long currentUserId = studentService.getAuthStudentId(authentication);
            studentService.findById(currentUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
            boolean removed = comment.getLikedBy().removeIf(student -> student.getId().equals(currentUserId));
            if (!removed) {
                responseMap.put(CODE, "400");
                responseMap.put(STATUS, "error");
                responseMap.put(MESSAGE, "Comment not liked by the user");
                return ResponseEntity.badRequest().body(responseMap);
            }

            comment.setLikes(Math.max(comment.getLikes() - 1, 0));
            commentService.save(comment);
            responseMap.put(CODE, "200");
            responseMap.put(STATUS, "success");
            responseMap.put(MESSAGE, "Unliked successfully");
            return ResponseEntity.ok().body(responseMap);
        }
        responseMap.put(CODE, "404");
        responseMap.put(STATUS, "error");
        responseMap.put(MESSAGE, "Comment not found");
        return ResponseEntity.badRequest().body(responseMap);
    }

    @Operation(summary = "Delete comment by student")
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteCommentByStudent(@RequestParam("commentId") Long commentId,
                                                                      Authentication authentication) {
        Map<String, Object> responseMap = new HashMap<>();
        Optional<Comment> optionalComment = commentService.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            if (comment.getStudent().getId().equals(studentService.getAuthStudentId(authentication))) {
                commentService.delete(comment);
                responseMap.put(CODE, "200");
                responseMap.put(STATUS, "success");
                responseMap.put(MESSAGE, "Comment deleted successfully");
                return ResponseEntity.ok().body(responseMap);
            } else {
                responseMap.put(CODE, "403");
                responseMap.put(STATUS, "error");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseMap);
            }
        } else {
            responseMap.put(CODE, "404");
            responseMap.put(STATUS, "error");
            responseMap.put(MESSAGE, "Comment not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMap);
        }
    }

}
