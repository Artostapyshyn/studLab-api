package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.Comment;
import com.artostapyshyn.studlabapi.entity.Reply;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface CommentService {

    Comment save(Comment comment);

    void addReplyToComment(Reply reply, Long parentId, Authentication authentication);

    Optional<Comment> findById(Long id);

    void delete(Comment comment);
}
