package com.artostapyshyn.studLabApi.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "event_comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "comment_text", nullable = false)
    private String commentText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comments_id")
    @JsonBackReference
    private Event event;

}