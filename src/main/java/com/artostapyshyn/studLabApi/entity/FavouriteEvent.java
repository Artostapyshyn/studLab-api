package com.artostapyshyn.studLabApi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "favourite_events")
public class FavouriteEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

}