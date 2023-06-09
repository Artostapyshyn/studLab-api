package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.Comment;
import com.artostapyshyn.studlabapi.entity.Reply;

import java.util.Optional;

public interface CommentService {

    Comment save(Comment comment);

    void addReplyToComment(Reply reply, Long parentId);

    Optional<Comment> findById(Long id);
}
