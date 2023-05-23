package com.artostapyshyn.studLabApi.service;

import com.artostapyshyn.studLabApi.entity.Comment;
import com.artostapyshyn.studLabApi.entity.Reply;

public interface CommentService {

    Comment save(Comment comment);

    void addReplyToComment(Reply reply, Long parentId);
}
