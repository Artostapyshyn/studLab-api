package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.dto.ReplyDto;
import com.artostapyshyn.studlabapi.entity.Reply;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface ReplyService {
    List<Reply> findReplyByCommentId(Long commentId);

    void addReplyToComment(ReplyDto replyDto, Long parentId, Authentication authentication);

    Optional<Reply> findById(Long id);

    Reply save(Reply reply);

    void delete(Reply reply);
}
