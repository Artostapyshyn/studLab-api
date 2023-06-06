package com.artostapyshyn.studlabapi.entity;

import com.artostapyshyn.studlabapi.enums.EventType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.List;
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

    @Column(name = "date_of_event", nullable = false)
    private String date;

    @Column(name = "venue", nullable = false)
    private String venue;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "name_of_event", nullable = false)
    private String nameOfEvent;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private EventType eventType;

    @Basic(fetch=FetchType.LAZY)
    @Column(name = "event_photo", nullable = false)
    private byte[] eventPhoto;

    @Column(name = "favorite_count")
    @ColumnDefault("0")
    private int favoriteCount;

    @Column(name = "date_of_creation")
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "eventId")
    private Set<Comment> eventComments;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Complaint> complaints;

    public void addComment(Comment comment) {
        eventComments.add(comment);
        comment.setEventId(this.getId());
    }

    @PrePersist
    private void init() {
        creationDate = LocalDateTime.now();
    }
}