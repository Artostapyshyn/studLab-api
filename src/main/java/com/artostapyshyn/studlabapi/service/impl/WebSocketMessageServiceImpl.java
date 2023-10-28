package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Message;
import com.artostapyshyn.studlabapi.service.WebSocketMessageService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class WebSocketMessageServiceImpl implements WebSocketMessageService {
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendMessages(List<Message> messages, String destination) {
        messagingTemplate.convertAndSend(destination, messages);
    }
}
