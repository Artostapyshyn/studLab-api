package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.dto.MessageRequest;
import com.artostapyshyn.studlabapi.entity.Message;
import com.artostapyshyn.studlabapi.service.MessageService;
import com.artostapyshyn.studlabapi.service.WebSocketMessageService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@AllArgsConstructor
public class SocketMessageController {
    private final MessageService messageService;

    private final WebSocketMessageService webSocketMessageService;

    @MessageMapping("/all")
    public void getAllMessages(@Payload MessageRequest messageRequest) {
        List<Message> messages = messageService.findAllMessagesByStudentId(messageRequest.getStudentId());
        webSocketMessageService.sendMessages(messages, "/topic/messages");
    }

}
