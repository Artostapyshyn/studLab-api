package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.dto.CommentDto;
import com.artostapyshyn.studlabapi.dto.ReplyDto;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.MESSAGE;

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

    @Operation(summary = "Add comment to an existing event")
    @PostMapping("/add")
    public ResponseEntity<List<Object>> addCommentToEvent(@RequestParam("eventId") Long eventId,
                                                          @RequestBody @NotNull CommentDto commentDto,
                                                          Authentication authentication) {
        List<Object> response = new ArrayList<>();
        Optional<Event> event = eventService.findEventById(eventId);
        if (event.isPresent()) {
            Optional<Student> optionalStudent = studentService.findById(studentService.getAuthStudentId(authentication));
            if (optionalStudent.isPresent()) {
                Student student = optionalStudent.get();
                Comment savedComment = commentService.addCommentToEvent(event.get(), commentDto, student);
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

    @Operation(summary = "Find comment by id.")
    @GetMapping("/find-by-id")
    public ResponseEntity<Comment> getCommentById(@RequestParam("commentId") Long commentId) {
        Comment comment = commentService.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't find comment by id - " + commentId));
        return ResponseEntity.ok().body(comment);
    }

    @Operation(summary = "Reply to comment")
    @PostMapping("/reply")
    public ResponseEntity<Map<String, Object>> addReplyToComment(@RequestBody @NotNull ReplyDto replyDto,
                                                                 @RequestParam("commentId") Long commentId, Authentication authentication) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            replyService.addReplyToComment(replyDto, commentId, authentication);
            responseMap.put(MESSAGE, "Replied successfully");
            return ResponseEntity.ok(responseMap);
        } catch (ResourceNotFoundException e) {
            responseMap.put(MESSAGE, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMap);
        }
    }

    @Operation(summary = "Find comment by id.")
    @GetMapping("/reply/find-by-id")
    public ResponseEntity<Reply> getReplyById(@RequestParam("replyId") Long replyId) {
        Reply reply = replyService.findById(replyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't find reply by id - " + replyId));
        return ResponseEntity.ok().body(reply);
    }

    @Operation(summary = "Get all event comments")
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

    @Operation(summary = "Get all replies")
    @GetMapping("/all-replies")
    public ResponseEntity<List<Reply>> getAllRepliesForComment(@RequestParam("commentId") Long commentId) {
        List<Reply> replies = replyService.findReplyByCommentId(commentId);
        return ResponseEntity.ok().body(replies);
    }

    @Operation(summary = "Like comment")
    @PostMapping("/like-comment")
    public ResponseEntity<Map<String, Object>> likeComment(@RequestParam("commentId") Long commentId,
                                                           Authentication authentication) {
        return handleLikeUnlikeComment(commentId, authentication, true);
    }

    @Operation(summary = "Unlike comment")
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
    public ResponseEntity<Map<String, Object>> deleteCommentByStudent(@RequestParam("commentId") Long commentId,
                                                                      Authentication authentication) {
        Optional<Comment> optionalComment = commentService.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            boolean hasAdminOrModeratorRole = isHasAdminOrModeratorRole(authentication);

            if (comment.getStudent().getId().equals(studentService.getAuthStudentId(authentication)) || hasAdminOrModeratorRole) {
                commentService.delete(comment);
                return ResponseEntity.ok().body(Collections.singletonMap(MESSAGE, "Comment deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete reply by student")
    @DeleteMapping("/delete-reply")
    public ResponseEntity<Map<String, Object>> deleteReplyByStudent(@RequestParam("replyId") Long replyId,
                                                                    Authentication authentication) {
        Optional<Reply> optionalReply = replyService.findById(replyId);
        if (optionalReply.isPresent()) {
            boolean hasAdminOrModeratorRole = isHasAdminOrModeratorRole(authentication);
            Reply reply = optionalReply.get();
            if (optionalReply.get().getStudent().getId().equals(studentService.getAuthStudentId(authentication)) || hasAdminOrModeratorRole) {
                replyService.delete(reply);
                return ResponseEntity.ok().body(Collections.singletonMap(MESSAGE, "Reply deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private static boolean isHasAdminOrModeratorRole(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        return authorities.stream()
                .anyMatch(grantedAuthority ->
                        grantedAuthority.getAuthority().equals("ROLE_ADMIN") ||
                                grantedAuthority.getAuthority().equals("ROLE_MODERATOR"));
    }

}
