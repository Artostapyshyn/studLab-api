package com.artostapyshyn.studLabApi.service.impl;

import com.artostapyshyn.studLabApi.entity.Comment;
import com.artostapyshyn.studLabApi.entity.Reply;
import com.artostapyshyn.studLabApi.repository.CommentRepository;
import com.artostapyshyn.studLabApi.repository.ReplyRepository;
import com.artostapyshyn.studLabApi.service.CommentService;
import com.artostapyshyn.studLabApi.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final ReplyRepository replyRepository;

    private final MessageService messageService;

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public void addReplyToComment(Reply reply, Long parentId) {
        Comment parentComment = commentRepository.findById(parentId)
                .orElseThrow(() -> new NotFoundException("Parent comment not found"));

        List<Reply> replies = parentComment.getReplies();
        replies.add(reply);

        reply.setComment(parentComment);
        reply.setStudent(parentComment.getStudent());

        commentRepository.save(parentComment);
        replyRepository.save(reply);

        Long studentId = parentComment.getStudent().getId();
        messageService.addMessageToStudent(studentId);
        messageService.updateNewMessageStatus(studentId, true);
    }

}