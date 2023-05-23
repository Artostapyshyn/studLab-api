package com.artostapyshyn.studLabApi.service;

import com.artostapyshyn.studLabApi.entity.Message;

import java.util.List;

public interface MessageService {

    Message save(Message message);

    List<Message> findAllMessagesByStudentId(Long id);

    void updateNewMessageStatus(Long studentId, boolean hasNewMessages);

    void addMessageToStudent(Long studentId);
}
