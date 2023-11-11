package com.artostapyshyn.studlabapi.entity;

import com.artostapyshyn.studlabapi.enums.MeetingType;
import com.artostapyshyn.studlabapi.util.CustomLocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
@Entity
@Table(name = "meetings")
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name_of_meeting", nullable = false)
    private String name;

    @Column(name = "date_of_meeting", nullable = false)
    @JsonFormat(pattern = "dd-MM HH:mm")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime date;

    @Column(name = "venue", nullable = false)
    private String venue;

    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_type", nullable = false)
    private MeetingType meetingType;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Student author;

    @Column(name = "event_name")
    private String eventName;

    @ManyToMany
    @JoinTable(
            name = "meeting_participants",
            joinColumns = @JoinColumn(name = "meeting_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Student> participants = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Meeting meeting)) return false;
        return Objects.equals(getId(), meeting.getId()) && Objects.equals(getName(), meeting.getName()) && Objects.equals(getDate(), meeting.getDate()) && Objects.equals(getVenue(), meeting.getVenue()) && Objects.equals(getDescription(), meeting.getDescription()) && getMeetingType() == meeting.getMeetingType() && Objects.equals(getEventName(), meeting.getEventName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDate(), getVenue(), getDescription(), getMeetingType(), getEventName());
    }
}
