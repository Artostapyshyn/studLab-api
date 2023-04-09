package com.artostapyshyn.studLabApi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "events")
public class Events {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
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

}