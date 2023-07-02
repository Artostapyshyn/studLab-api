package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Message;
import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.service.MessageService;
import com.artostapyshyn.studlabapi.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService messageService;

    private final StudentService studentService;

    @Operation(summary = "Get all messages by student id")
    @GetMapping("/all")
    public ResponseEntity<List<Message>> getAllMessages(Authentication authentication) {
        List<Message> messages = messageService.findAllMessagesByStudentId(getAuthStudentId(authentication));
        return ResponseEntity.ok().body(messages.stream().toList());
    }

    @Operation(summary = "Mark messages as read")
    @PostMapping("/mark-as-read")
    public ResponseEntity<Map<String, Object>> markAllMessagesAsRead(Authentication authentication) {
        Map<String, Object> responseMap = new HashMap<>();
        messageService.updateNewMessageStatus(getAuthStudentId(authentication), false);
        responseMap.put("status", "marked-as-read");
        return ResponseEntity.ok().body(responseMap);
    }

    @Operation(summary = "Delete message by id")
    @DeleteMapping("/delete-by-id")
    public ResponseEntity<Void> deleteMessage(@RequestParam("messageId") Long messageId) {
        Optional<Message> optionalMessage = messageService.findById(messageId);
        if (optionalMessage.isPresent()) {
            messageService.deleteById(messageId);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private Long getAuthStudentId(Authentication authentication) {
        String studentEmail = authentication.getName();
        Student student = studentService.findByEmail(studentEmail);
        return student.getId();
    }
}
