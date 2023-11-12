package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Comment;
import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomEvent;
import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomStudent;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAll() {
        Event event = createRandomEvent();
        entityManager.persist(event);

        Student student = createRandomStudent();
        entityManager.persist(student);

        Comment comment = new Comment();
        comment.setStudent(student);
        comment.setCommentText("Test");
        comment.setEventId(event.getId());
        entityManager.persist(comment);

        Comment comment2 = new Comment();
        comment2.setStudent(student);
        comment2.setCommentText("Test2");
        comment2.setEventId(event.getId());
        entityManager.persist(comment2);
        Iterable<Comment> comments = commentRepository.findAll();
        assertEquals(2, comments.spliterator().getExactSizeIfKnown());

    }
}