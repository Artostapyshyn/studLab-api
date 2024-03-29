package com.artostapyshyn.studlabapi.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
@Entity
@Table(name = "replies", indexes = {
        @Index(name = "idx_student_replies", columnList = "student_id"),
        @Index(name = "idx_comment_replies", columnList = "comment_id")
})
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "reply_text", nullable = false)
    private String replyText;

    @JsonBackReference
    @JsonIgnore
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reply reply)) return false;
        return Objects.equals(getId(), reply.getId())
                && Objects.equals(getReplyText(), reply.getReplyText());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getReplyText());
    }

    @Override
    public String toString() {
        return "Reply{" +
                "id=" + id +
                ", replyText='" + replyText + '\'' +
                ", comment=" + comment +
                ", student=" + student +
                '}';
    }
}