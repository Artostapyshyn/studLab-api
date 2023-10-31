package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.dto.CommentDto;
import com.artostapyshyn.studlabapi.dto.CommentMessageRequest;
import com.artostapyshyn.studlabapi.dto.EventMessageRequest;
import com.artostapyshyn.studlabapi.dto.ReplyDto;
import com.artostapyshyn.studlabapi.entity.Comment;
import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.entity.Reply;
import com.artostapyshyn.studlabapi.service.*;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@AllArgsConstructor
public class SocketCommentController {

    private final WebSocketMessageService webSocketMessageService;

    private final EventService eventService;

    private final ReplyService replyService;

    private final ModelMapper modelMapper;

    @MessageMapping("/all-replies")
    public void getAllRepliesForComment(@Payload CommentMessageRequest commentMessageRequest) {
        List<Reply> replies = replyService.findReplyByCommentId(commentMessageRequest.getCommentId());
        List<ReplyDto> replyDtoList = replies.stream()
                .map(reply -> modelMapper.map(reply, ReplyDto.class))
                .toList();
        webSocketMessageService.sendPayloads(replyDtoList, "/topic/replies");
    }

    @MessageMapping("/all-comments")
    public void getCommentsForEvent(@Payload EventMessageRequest eventMessageRequest) {
        Optional<Event> optionalEvent = eventService.findEventById(eventMessageRequest.getEventId());
        if (optionalEvent.isPresent()) {
            List<Comment> commentList = getCommentsForEvent(optionalEvent.get());
            List<CommentDto> commentDtoList = commentList.stream()
                    .map(comment -> modelMapper.map(comment, CommentDto.class))
                    .toList();
            webSocketMessageService.sendPayloads(commentDtoList, "/topic/comments");
        }
    }

    private List<Comment> getCommentsForEvent(Event event) {
        Set<Comment> comments = event.getEventComments();
        return new ArrayList<>(comments);
    }
}
