package com.artostapyshyn.studlabapi.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "event_comments",
        indexes = {
                @Index(name = "index_comments_student_id", columnList = "student_id"),
                @Index(name = "index_event_id", columnList = "event_id")
        })
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "comment_text", nullable = false)
    private String commentText;

    @Column(name = "comment_likes")
    @ColumnDefault("0")
    private int likes;

    @JsonBackReference
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<Reply> replies;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("likedBy")
    @ManyToMany
    @JoinTable(
            name = "comment_likes",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Student> likedBy = new HashSet<>();

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment comment)) return false;
        return getLikes() == comment.getLikes()
                && Objects.equals(getId(), comment.getId())
                && Objects.equals(getCommentText(), comment.getCommentText())
                && Objects.equals(getReplies(), comment.getReplies())
                && Objects.equals(getLikedBy(), comment.getLikedBy())
                && Objects.equals(getEventId(), comment.getEventId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCommentText(), getLikes(), getReplies(), getLikedBy(), getEventId());
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", commentText='" + commentText + '\'' +
                ", likes=" + likes +
                ", replies=" + replies +
                ", likedBy=" + likedBy +
                ", eventId=" + eventId +
                ", student=" + student +
                '}';
    }
}