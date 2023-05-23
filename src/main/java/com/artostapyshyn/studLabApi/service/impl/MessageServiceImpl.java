package com.artostapyshyn.studLabApi.service.impl;

import com.artostapyshyn.studLabApi.entity.Message;
import com.artostapyshyn.studLabApi.entity.Student;
import com.artostapyshyn.studLabApi.repository.MessageRepository;
import com.artostapyshyn.studLabApi.service.MessageService;
import com.artostapyshyn.studLabApi.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    private final StudentService studentService;

    @Override
    public Message save(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public List<Message> findAllMessagesByStudentId(Long id) {
        return messageRepository.findAllMessagesByStudentId(id);
    }

    @Override
    public void addMessageToStudent(Long studentId) {
        Student student = studentService.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));

            Message message = new Message();
            message.setContent("Вам надійшла відповідь на ваш коментар.");
            message.setSentTime(LocalDateTime.now());
            message.setStudent(student);

            student.getMessages().add(message);

            studentService.save(student);
            updateNewMessageStatus(studentId, true);
    }

    @Override
    public void updateNewMessageStatus(Long studentId, boolean hasNewMessages) {
        Student student = studentService.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        student.setHasNewMessages(hasNewMessages);
        studentService.save(student);

    }

}