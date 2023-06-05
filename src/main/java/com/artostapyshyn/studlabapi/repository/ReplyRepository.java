package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
}