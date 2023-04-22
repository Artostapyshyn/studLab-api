package com.artostapyshyn.studLabApi.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false)
    private Long id;

    @Column(name = "date_of_event")
    private String dateOfEvent;

    @Column(name = "venue", nullable = false)
    private String venue;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "name_of_event", nullable = false)
    private String nameOfEvent;

    @Lob
    @Column(name = "event_photo")
    private byte[] eventPhoto;

    @JsonBackReference("event-comments")
    @OneToMany(mappedBy = "event")
    private Set<Comment> eventComments;

    public void addComment(Comment comment) {
        eventComments.add(comment);
        comment.setEvent(this);
    }

}