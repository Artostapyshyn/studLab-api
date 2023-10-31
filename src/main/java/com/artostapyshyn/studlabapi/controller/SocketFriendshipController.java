package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.dto.FriendshipDTO;
import com.artostapyshyn.studlabapi.dto.MessageRequest;
import com.artostapyshyn.studlabapi.service.FriendshipService;
import com.artostapyshyn.studlabapi.service.WebSocketMessageService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@AllArgsConstructor
public class SocketFriendshipController {
    private final FriendshipService friendshipService;

    private final WebSocketMessageService webSocketMessageService;

    @MessageMapping("/all-friends")
    public void getAllMessages(@Payload MessageRequest messageRequest) {
        List<FriendshipDTO> friends = friendshipService.findAllByStudentId(messageRequest.getStudentId());
        webSocketMessageService.sendPayloads(friends, "/topic/friends");
    }

}
