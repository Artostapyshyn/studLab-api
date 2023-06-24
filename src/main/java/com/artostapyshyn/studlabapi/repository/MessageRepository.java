package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllMessagesByStudentId(Long id);
}
