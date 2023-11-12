package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Comment;
import com.artostapyshyn.studlabapi.repository.CommentRepository;
import com.artostapyshyn.studlabapi.repository.ReplyRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomComment;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MessageServiceImpl messageService;

    @Mock
    private ReplyRepository replyRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void save() {
        Comment comment = createRandomComment();

        when(commentRepository.save(comment)).thenReturn(comment);

        Comment savedComment = commentService.save(comment);

        assertNotNull(savedComment);
        assertEquals(comment.getId(), savedComment.getId());
        assertEquals(comment.getCommentText(), savedComment.getCommentText());
    }

    @Test
    void findById() {
        Comment comment = createRandomComment();

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        Optional<Comment> foundComment = commentService.findById(comment.getId());

        assertNotNull(foundComment);

        assertTrue(foundComment.isPresent());
        assertEquals(comment.getId(), foundComment.get().getId());
        assertEquals(comment.getCommentText(), foundComment.get().getCommentText());
    }

    @Test
    void delete() {
        Comment comment = createRandomComment();
        commentService.delete(comment);
        verify(commentRepository, times(1)).delete(comment);
    }
}