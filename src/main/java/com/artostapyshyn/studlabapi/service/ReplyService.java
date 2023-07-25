package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.Reply;

import java.util.List;
import java.util.Optional;

public interface ReplyService {
    List<Reply> findReplyByCommentId(Long commentId);

    Optional<Reply> findById(Long id);

    Reply save(Reply reply);

    void delete(Reply reply);
}
