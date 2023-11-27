package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Message;
import com.artostapyshyn.studlabapi.service.MessageService;
import com.artostapyshyn.studlabapi.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;
import static io.micrometer.core.instrument.binder.BaseUnits.MESSAGES;

@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/messages")
@CrossOrigin(maxAge = 3600, origins = "*")
public class MessageController {

    private final MessageService messageService;

    private final StudentService studentService;

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllMessages(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put(MESSAGES, messageService.findAllMessagesByStudentId(studentService.getAuthStudentId(authentication)));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Mark messages as read")
    @PostMapping("/mark-as-read")
    public ResponseEntity<Map<String, Object>> markAllMessagesAsRead(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        messageService.updateNewMessageStatus(studentService.getAuthStudentId(authentication), false);

        response.put(MESSAGE, "All messages marked as read");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete message by id")
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
