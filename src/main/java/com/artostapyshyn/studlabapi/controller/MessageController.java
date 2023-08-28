package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Message;
import com.artostapyshyn.studlabapi.service.MessageService;
import com.artostapyshyn.studlabapi.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/messages")
@CrossOrigin(maxAge = 3600, origins = "*")
public class MessageController {

    private final MessageService messageService;

    private final StudentService studentService;

    @Operation(summary = "Get all messages by student id",
            security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/all")
    public ResponseEntity<List<Message>> getAllMessages(Authentication authentication) {
        List<Message> messages = messageService.findAllMessagesByStudentId(studentService.getAuthStudentId(authentication));
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "Mark messages as read",
            security = @SecurityRequirement(name = "basicAuth"))
    @PostMapping("/mark-as-read")
    public ResponseEntity<Map<String, Object>> markAllMessagesAsRead(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        messageService.updateNewMessageStatus(studentService.getAuthStudentId(authentication), false);

        response.put(MESSAGE, "All messages marked as read");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete message by id",
            security = @SecurityRequirement(name = "basicAuth"))
    @DeleteMapping("/delete-by-id")
    public ResponseEntity<Map<String, Object>> deleteMessage(@RequestParam("messageId") Long messageId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Message> optionalMessage = messageService.findById(messageId);
        if (optionalMessage.isPresent()) {
            messageService.deleteById(messageId);
            response.put(MESSAGE, "Message deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
