package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Comment;
import com.artostapyshyn.studlabapi.entity.Reply;
import com.artostapyshyn.studlabapi.exception.CommentNotFoundException;
import com.artostapyshyn.studlabapi.repository.CommentRepository;
import com.artostapyshyn.studlabapi.repository.ReplyRepository;
import com.artostapyshyn.studlabapi.service.CommentService;
import com.artostapyshyn.studlabapi.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final ReplyRepository replyRepository;

    private final MessageService messageService;

    @Transactional
    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public void addReplyToComment(Reply reply, Long parentId) {
        Comment parentComment = commentRepository.findById(parentId)
                .orElseThrow(() -> new CommentNotFoundException("Parent comment not found"));

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

    @Override
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    @Transactional
    @Override
    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }
}