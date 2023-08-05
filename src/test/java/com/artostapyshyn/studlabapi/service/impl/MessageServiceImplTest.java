package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Message;
import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomMessages;
import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomStudent;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private StudentServiceImpl studentService;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Test
    void save() {
        Message message = createRandomMessages();

        when(messageRepository.save(message)).thenReturn(message);
        Message savedMessage = messageService.save(message);

        assertEquals(message, savedMessage);
        assertNotNull(savedMessage);
        verify(messageRepository, times(1)).save(message);
    }

    @Test
    void findById() {
        Message message = createRandomMessages();

        when(messageRepository.findById(message.getId())).thenReturn(Optional.of(message));
        Optional<Message> optionalMessage = messageService.findById(message.getId());
        Message resultMessage = optionalMessage.orElse(null);

        assertEquals(message, resultMessage);
        verify(messageRepository, times(1)).findById(message.getId());

    }

    @Test
    void deleteById() {
        Message message = createRandomMessages();

        messageService.deleteById(message.getId());
        verify(messageRepository, times(1)).deleteById(message.getId());
    }

    @Test
    void findAllMessagesByStudentId() {
        Long studentId = 1L;
        List<Message> expectedList = List.of(createRandomMessages(), createRandomMessages());

        when(messageRepository.findAllMessagesByStudentId(studentId)).thenReturn(expectedList);

        List<Message> resultList = messageService.findAllMessagesByStudentId(studentId);

        assertEquals(expectedList, resultList);
        verify(messageRepository, times(1)).findAllMessagesByStudentId(studentId);
    }

    @Test
    void updateNewMessageStatus() {
        Long studentId = 1L;
        boolean hasNewMessages = true;

        Student student = createRandomStudent();
        when(studentService.findById(studentId)).thenReturn(Optional.of(student));

        messageService.updateNewMessageStatus(studentId, hasNewMessages);

        verify(studentService, times(1)).findById(studentId);
        assertEquals(hasNewMessages, student.getHasNewMessages());
        verify(studentService, times(1)).save(student);
    }


}
