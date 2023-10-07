package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(attributePaths = "replies")
    List<Comment> findAll();
}