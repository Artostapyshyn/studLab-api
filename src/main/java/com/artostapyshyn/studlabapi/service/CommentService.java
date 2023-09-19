package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.dto.CommentDto;
import com.artostapyshyn.studlabapi.dto.StudentDto;
import com.artostapyshyn.studlabapi.entity.Comment;
import com.artostapyshyn.studlabapi.entity.Event;

import java.util.Optional;

public interface CommentService {

    Comment save(Comment comment);

    Comment addCommentToEvent(Event event, CommentDto commentDto, StudentDto student);

    Optional<Comment> findById(Long id);

    void delete(Comment comment);
}
