package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Reply;
import com.artostapyshyn.studlabapi.repository.ReplyRepository;
import com.artostapyshyn.studlabapi.service.ReplyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;

    @Override
    public List<Reply> findReplyByCommentId(Long commentId) {
        return replyRepository.findReplyByCommentId(commentId);
    }

    @Override
    public Optional<Reply> findById(Long id) {
        return replyRepository.findById(id);
    }

    @Override
    public Reply save(Reply reply) {
        return replyRepository.save(reply);
    }

    @Override
    public void delete(Reply reply) {
        replyRepository.delete(reply);
    }
}
