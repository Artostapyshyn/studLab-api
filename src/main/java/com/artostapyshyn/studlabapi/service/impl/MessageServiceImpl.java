package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Message;
import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.exception.exceptions.ResourceNotFoundException;
import com.artostapyshyn.studlabapi.repository.MessageRepository;
import com.artostapyshyn.studlabapi.service.MessageService;
import com.artostapyshyn.studlabapi.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    private final StudentService studentService;

    private final WebSocketMessageServiceImpl webSocketMessageService;

    @Transactional
    @Override
    public Message save(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public Optional<Message> findById(Long id) {
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> findAllMessagesByStudentId(Long id) {
        return messageRepository.findAllMessagesByStudentId(id);
    }

    @Transactional
    @Override
    public void addMessageToStudent(Long studentId, String messageContent) {
        Student student = studentService.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Message message = new Message();
        message.setContent(messageContent);
        message.setSentTime(LocalDateTime.now());
        message.setStudent(student);

        student.getMessages().add(message);
        messageRepository.save(message);
        studentService.save(student);

        webSocketMessageService.sendPayloads(Collections.singletonList(message), "/topic/messages");
        updateNewMessageStatus(studentId, true);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        messageRepository.deleteById(id);
    }

    @Override
    public void updateNewMessageStatus(Long studentId, boolean hasNewMessages) {
        Student student = studentService.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        student.setHasNewMessages(hasNewMessages);
        studentService.save(student);
    }
}
