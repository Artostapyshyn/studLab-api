package com.artostapyshyn.studlabapi.entity;

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
@Table(name = "favourite_events")
public class FavouriteEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FavouriteEvent that)) return false;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getStudentId(), that.getStudentId())
                && Objects.equals(getEvent(), that.getEvent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getStudentId(), getEvent());
    }

    @Override
    public String toString() {
        return "FavouriteEvent{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", event=" + event +
                '}';
    }
}