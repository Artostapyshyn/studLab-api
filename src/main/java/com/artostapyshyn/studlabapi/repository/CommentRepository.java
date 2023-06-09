package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}