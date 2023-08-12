package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Message;
import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.exception.exceptions.ResourceNotFoundException;
import com.artostapyshyn.studlabapi.repository.MessageRepository;
import com.artostapyshyn.studlabapi.service.MessageService;
import com.artostapyshyn.studlabapi.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    private final StudentService studentService;

    @Transactional
    @Override
    @CacheEvict(value = "messagesByStudentId", beforeInvocation = true)
    public Message save(Message message) {
        return messageRepository.save(message);
    }

    @Override
    @Cacheable("messagesById")
    public Optional<Message> findById(Long id) {
        return messageRepository.findById(id);
    }

    @Override
    @Cacheable("messagesByStudentId")
    public List<Message> findAllMessagesByStudentId(Long id) {
        return messageRepository.findAllMessagesByStudentId(id);
    }

    @Override
    @CacheEvict(value = "messagesByStudentId")
    public void addMessageToStudent(Long studentId) {
        Student student = studentService.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Message message = new Message();
        message.setContent("Вам надійшла відповідь на ваш коментар події ");
        message.setSentTime(LocalDateTime.now());
        message.setStudent(student);

        student.getMessages().add(message);

        studentService.save(student);
        updateNewMessageStatus(studentId, true);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"messagesById", "messagesByStudentId"}, beforeInvocation = true)
    public void deleteById(Long id) {
        messageRepository.deleteById(id);
    }

    @Override
    @CacheEvict(value = "messagesByStudentId")
    public void updateNewMessageStatus(Long studentId, boolean hasNewMessages) {
        Student student = studentService.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        student.setHasNewMessages(hasNewMessages);
        studentService.save(student);
    }
}
