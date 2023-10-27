package com.artostapyshyn.studlabapi.entity;

import com.artostapyshyn.studlabapi.enums.EventType;
import com.artostapyshyn.studlabapi.util.CustomLocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
@Entity
@Table(name = "events", indexes = {
        @Index(name = "index_favoriteCount", columnList = "favorite_count"),
        @Index(name = "index_date", columnList = "date_of_event"),
        @Index(name = "index_creationDate", columnList = "date_of_creation")
})
@SecondaryTable(name = "event_tags",
        indexes = {
                @Index(name = "index_event_tags_event_id", columnList = "event_id"),
                @Index(name = "index_event_tags_tag_id", columnList = "tag_id")
        })
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false)
    private Long id;

    @Column(name = "date_of_event", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dddd HH:mm")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime date;

    @Column(name = "end_date_of_event")
    @JsonFormat(pattern = "dd-MM HH:mm")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endDate;

    @Column(name = "venue", nullable = false)
    private String venue;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "name_of_event", nullable = false)
    private String nameOfEvent;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "event_photo", nullable = false)
    private byte[] eventPhoto;

    @Column(name = "favorite_count")
    @ColumnDefault("0")
    private int favoriteCount;

    @Column(name = "date_of_creation")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    @CreationTimestamp
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "eventId", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<Comment> eventComments;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "event_tags",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

    public void addComment(Comment comment) {
        eventComments.add(comment);
        comment.setEventId(this.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event event)) return false;
        return getFavoriteCount() == event.getFavoriteCount()
                && Objects.equals(getId(), event.getId())
                && Objects.equals(getDate(), event.getDate())
                && Objects.equals(getVenue(), event.getVenue())
                && Objects.equals(getDescription(), event.getDescription())
                && Objects.equals(getNameOfEvent(), event.getNameOfEvent())
                && getEventType() == event.getEventType()
                && Objects.equals(getCreationDate(), event.getCreationDate())
                && Objects.equals(getEventComments(), event.getEventComments());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDate(), getVenue(), getDescription(), getNameOfEvent(),
                getEventType(), getFavoriteCount(), getCreationDate(), getEventComments());
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", date=" + date +
                ", venue='" + venue + '\'' +
                ", description='" + description + '\'' +
                ", nameOfEvent='" + nameOfEvent + '\'' +
                ", eventType=" + eventType +
                ", eventPhoto=" + Arrays.toString(eventPhoto) +
                ", favoriteCount=" + favoriteCount +
                ", creationDate=" + creationDate +
                ", eventComments=" + eventComments +
                '}';
    }
}