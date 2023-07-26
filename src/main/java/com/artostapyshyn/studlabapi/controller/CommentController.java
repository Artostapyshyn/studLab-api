package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Comment;
import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.entity.Reply;
import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.exception.exceptions.ResourceNotFoundException;
import com.artostapyshyn.studlabapi.service.CommentService;
import com.artostapyshyn.studlabapi.service.EventService;
import com.artostapyshyn.studlabapi.service.ReplyService;
import com.artostapyshyn.studlabapi.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;

@Log4j2
@Validated
@RestController
@RequestMapping("/api/v1/comments")
@AllArgsConstructor
@CrossOrigin(maxAge = 3600, origins = "*")
public class CommentController {

    private final EventService eventService;

    private final CommentService commentService;

    private final StudentService studentService;

    private final ReplyService replyService;

    @PostMapping("/add")
    public ResponseEntity<List<Object>> addCommentToEvent(@RequestParam("eventId") Long eventId,
                                                          @RequestBody @NotNull Comment comment,
                                                          Authentication authentication) {
        List<Object> response = new ArrayList<>();
        Optional<Event> event = eventService.findEventById(eventId);
        if (event.isPresent()) {
            Optional<Student> optionalStudent = studentService.findById(studentService.getAuthStudentId(authentication));
            if (optionalStudent.isPresent()) {
                Student student = optionalStudent.get();
                Comment savedComment = addCommentToEvent(event.get(), comment, student);
                response.add(savedComment);
                return ResponseEntity.ok().body(response);
            } else {
                response.add("Student not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } else {
            response.add("Event not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    private Comment addCommentToEvent(Event event, Comment comment, Student student) {
        comment.setStudent(student);
        event.addComment(comment);
        commentService.save(comment);
        return comment;
    }

    @Operation(summary = "Reply to comment")
    @PostMapping("/reply")
    public ResponseEntity<Map<String, Object>> addReplyToComment(@RequestBody @NotNull Reply reply,
                                                                 @RequestParam("commentId") Long commentId, Authentication authentication) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            commentService.addReplyToComment(reply, commentId, authentication);
            responseMap.put(MESSAGE, "Replied successfully");
            return ResponseEntity.ok(responseMap);
        } catch (ResourceNotFoundException e) {
            responseMap.put(MESSAGE, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMap);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Comment>> getCommentsForEvent(@RequestParam("eventId") Long eventId) {
        Optional<Event> optionalEvent = eventService.findEventById(eventId);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            List<Comment> commentList = getCommentsForEvent(event);
            return ResponseEntity.ok(commentList);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private List<Comment> getCommentsForEvent(Event event) {
        Set<Comment> comments = event.getEventComments();
        return new ArrayList<>(comments);
    }

    @GetMapping("/all-replies")
    public ResponseEntity<List<Reply>> getAllRepliesForComment(@RequestParam("commentId") Long commentId) {
        List<Reply> replies = replyService.findReplyByCommentId(commentId);
        return ResponseEntity.ok().body(replies);
    }

    @PostMapping("/like-comment")
    public ResponseEntity<Map<String, Object>> likeComment(@RequestParam("commentId") Long commentId,
                                                           Authentication authentication) {
        return handleLikeUnlikeComment(commentId, authentication, true);
    }

    @PostMapping("/unlike-comment")
    public ResponseEntity<Map<String, Object>> unlikeComment(@RequestParam("commentId") Long commentId,
                                                             Authentication authentication) {
        return handleLikeUnlikeComment(commentId, authentication, false);
    }

    private ResponseEntity<Map<String, Object>> handleLikeUnlikeComment(Long commentId, Authentication authentication, boolean isLike) {
        Map<String, Object> responseMap = new HashMap<>();
        Optional<Comment> optionalComment = commentService.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            Long currentUserId = studentService.getAuthStudentId(authentication);
            if ((isLike && comment.getLikedBy().stream().anyMatch(student -> student.getId().equals(currentUserId))) ||
                    (!isLike && comment.getLikedBy().removeIf(student -> student.getId().equals(currentUserId)))) {
                responseMap.put(MESSAGE, isLike ? "Comment already liked by the user" : "Comment not liked by the user");
                return ResponseEntity.badRequest().body(responseMap);
            }

            if (isLike) {
                comment.setLikes(comment.getLikes() + 1);
                Student currentUser = studentService.findById(currentUserId)
                        .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
                comment.getLikedBy().add(currentUser);
            } else {
                comment.setLikes(Math.max(comment.getLikes() - 1, 0));
            }

            commentService.save(comment);
            responseMap.put(MESSAGE, isLike ? "Liked successfully" : "Unliked successfully");
            return ResponseEntity.ok().body(responseMap);
        }

        responseMap.put(MESSAGE, "Comment not found");
        return ResponseEntity.badRequest().body(responseMap);
    }

    @Operation(summary = "Delete comment by student")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCommentByStudent(@RequestParam("commentId") Long commentId,
                                                       Authentication authentication) {
        Optional<Comment> optionalComment = commentService.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            if (comment.getStudent().getId().equals(studentService.getAuthStudentId(authentication))) {
                commentService.delete(comment);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete reply by student")
    @DeleteMapping("/delete-reply")
    public ResponseEntity<Void> deleteReplyByStudent(@RequestParam("replyId") Long replyId,
                                                       Authentication authentication) {
        Optional<Reply> optionalReply = replyService.findById(replyId);
        if (optionalReply.isPresent()) {
            Reply reply = optionalReply.get();
            if (optionalReply.get().getStudent().getId().equals(studentService.getAuthStudentId(authentication))) {
                replyService.delete(reply);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
