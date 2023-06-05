package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.Message;

import java.util.List;

public interface MessageService {

    Message save(Message message);

    List<Message> findAllMessagesByStudentId(Long id);

    void updateNewMessageStatus(Long studentId, boolean hasNewMessages);

    void addMessageToStudent(Long studentId);
}
