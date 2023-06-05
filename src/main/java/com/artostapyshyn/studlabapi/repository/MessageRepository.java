package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllMessagesByStudentId(Long id);
}
