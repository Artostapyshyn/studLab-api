package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.dto.CommentDto;
import com.artostapyshyn.studlabapi.dto.StudentDto;
import com.artostapyshyn.studlabapi.entity.Comment;
import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.entity.Reply;
import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.exception.exceptions.ResourceNotFoundException;
import com.artostapyshyn.studlabapi.repository.CommentRepository;
import com.artostapyshyn.studlabapi.repository.ReplyRepository;
import com.artostapyshyn.studlabapi.service.CommentService;
import com.artostapyshyn.studlabapi.service.EventService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final EventService eventService;

    private final ReplyRepository replyRepository;

    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment addCommentToEvent(Event commentedEvent, CommentDto commentDto, StudentDto studentDto) {
        Event event = eventService.findEventById(commentedEvent.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        Comment comment = new Comment();
        comment.setCommentText(commentDto.getCommentText());
        Student student = modelMapper.map(studentDto, Student.class);
        comment.setStudent(student);
        event.addComment(comment);
        commentRepository.save(comment);
        return comment;
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    @Transactional
    @Override
    public void delete(Comment comment) {
        List<Reply> replies = replyRepository.findReplyByCommentId(comment.getId());
        replyRepository.deleteAll(replies);
        commentRepository.delete(comment);
    }
}