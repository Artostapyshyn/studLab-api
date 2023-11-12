package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Comment;
import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.entity.Reply;
import com.artostapyshyn.studlabapi.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.artostapyshyn.studlabapi.util.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class ReplyRepositoryTest {

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findReplyByCommentId() {
        Event event = createRandomEvent();
        entityManager.persist(event);

        Student student = createRandomStudent();
        entityManager.persist(student);

        Comment comment = createRandomComment();
        comment.setEventId(event.getId());
        comment.setStudent(student);
        entityManager.persist(comment);

        Reply reply = new Reply();
        reply.setComment(comment);
        reply.setReplyText("text");
        entityManager.persist(reply);

        entityManager.flush();

        List<Reply> found = replyRepository.findReplyByCommentId(comment.getId());
        assertEquals(1, found.size());
        assertEquals(reply, found.iterator().next());
    }
}