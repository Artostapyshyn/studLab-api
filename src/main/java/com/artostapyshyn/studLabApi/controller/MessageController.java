package com.artostapyshyn.studLabApi.controller;

import com.artostapyshyn.studLabApi.entity.Message;
import com.artostapyshyn.studLabApi.entity.Student;
import com.artostapyshyn.studLabApi.service.MessageService;
import com.artostapyshyn.studLabApi.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(maxAge = 3600)
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/messages")
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
    public ResponseEntity<?> markAllMessagesAsRead(Authentication authentication) {
        Map<String, Object> responseMap = new HashMap<>();
        messageService.updateNewMessageStatus(getAuthStudentId(authentication), false);
        responseMap.put("status", "marked-as-read");
        return ResponseEntity.ok().body(responseMap);
    }

    private Long getAuthStudentId(Authentication authentication) {
        String studentEmail = authentication.getName();
        Student student = studentService.findByEmail(studentEmail);
        return student.getId();
    }

}
