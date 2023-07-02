package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.Message;

import java.util.List;
import java.util.Optional;

public interface MessageService {

    Message save(Message message);

    Optional<Message> findById(Long id);

    List<Message> findAllMessagesByStudentId(Long id);

    void updateNewMessageStatus(Long studentId, boolean hasNewMessages);

    void addMessageToStudent(Long studentId);

    void deleteById(Long id);
}
