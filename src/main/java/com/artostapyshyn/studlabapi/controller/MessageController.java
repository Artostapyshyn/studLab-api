package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Message;
import com.artostapyshyn.studlabapi.service.MessageService;
import com.artostapyshyn.studlabapi.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService messageService;

    private final StudentService studentService;

    @Operation(summary = "Get all messages by student id")
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllMessages(Authentication authentication) {
        List<Message> messages = messageService.findAllMessagesByStudentId(studentService.getAuthStudentId(authentication));

        Map<String, Object> response = new HashMap<>();
        response.put(CODE, "200");
        response.put(STATUS, "success");
        response.put(MESSAGE, "All messages retrieved successfully");
        response.put("messages", messages);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Mark messages as read")
    @PostMapping("/mark-as-read")
    public ResponseEntity<Map<String, Object>> markAllMessagesAsRead(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        messageService.updateNewMessageStatus(studentService.getAuthStudentId(authentication), false);

        response.put(CODE, "200");
        response.put(STATUS, "success");
        response.put(MESSAGE, "All messages marked as read");

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete message by id")
    @DeleteMapping("/delete-by-id")
    public ResponseEntity<Map<String, Object>> deleteMessage(@RequestParam("messageId") Long messageId) {
        Optional<Message> optionalMessage = messageService.findById(messageId);
        if (optionalMessage.isPresent()) {
            messageService.deleteById(messageId);

            Map<String, Object> response = new HashMap<>();
            response.put(CODE, "200");
            response.put(STATUS, "success");
            response.put(MESSAGE, "Message deleted successfully");

            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put(CODE, "404");
            response.put(STATUS, "error");
            response.put(MESSAGE, "Message not found");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
