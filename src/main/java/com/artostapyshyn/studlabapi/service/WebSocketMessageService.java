package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.Message;

import java.util.List;

public interface WebSocketMessageService {
    void sendMessages(List<Message> messages, String destination);
}
