package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Message;
import com.artostapyshyn.studlabapi.entity.Student;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomMessages;
import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomStudent;

@ActiveProfiles("test")
@DataJpaTest
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAllMessagesByStudentId() {
        Student student = createRandomStudent();
        entityManager.persist(student);
        entityManager.flush();

        Message message = createRandomMessages();
        message.setStudent(student);
        entityManager.persist(message);
        entityManager.flush();

        List<Message> messages = messageRepository.findAllMessagesByStudentId(student.getId());

        Assertions.assertNotNull(messages);
        Assertions.assertEquals(1, messages.size());
    }
}