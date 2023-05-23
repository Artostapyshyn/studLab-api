package com.artostapyshyn.studLabApi.repository;

import com.artostapyshyn.studLabApi.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
}