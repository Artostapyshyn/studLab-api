package com.artostapyshyn.studLabApi.controller;

import com.artostapyshyn.studLabApi.entity.Message;
import com.artostapyshyn.studLabApi.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "Get all messages by student id")
    @GetMapping("/all")
    public ResponseEntity<List<Message>> getAllMessages(@RequestParam Long studentId) {
        List<Message> messages = messageService.findAllMessagesByStudentId(studentId);
        return ResponseEntity.ok().body(messages.stream().toList());
    }

    @Operation(summary = "Mark messages as read")
    @PostMapping("/mark-as-read")
    public ResponseEntity<?> markAllMessagesAsRead(@RequestParam Long studentId) {
        Map<String, String> responseMap = new HashMap<>();
        messageService.updateNewMessageStatus(studentId, false);
        responseMap.put("status", "marked-as-read");
        return ResponseEntity.ok().body(responseMap);
    }

}
