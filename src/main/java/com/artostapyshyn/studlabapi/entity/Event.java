package com.artostapyshyn.studlabapi.entity;

import com.artostapyshyn.studlabapi.enums.EventType;
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
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false)
    private Long id;

    @Column(name = "date_of_event", nullable = false)
    private LocalDateTime date;

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
    @CreationTimestamp
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "eventId")
    private Set<Comment> eventComments;

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
                && Arrays.equals(getEventPhoto(), event.getEventPhoto())
                && Objects.equals(getCreationDate(), event.getCreationDate())
                && Objects.equals(getEventComments(), event.getEventComments());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getId(), getDate(), getVenue(), getDescription(), getNameOfEvent(),
                getEventType(), getFavoriteCount(), getCreationDate(), getEventComments());
        result = 31 * result + Arrays.hashCode(getEventPhoto());
        return result;
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