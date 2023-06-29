package com.artostapyshyn.studlabapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id", nullable = false)
    private Long id;

    @Column(name = "course_link", nullable = false)
    private String courseLink;

    @Column(name = "course_description", nullable = false)
    private String courseDescription;

    @Column(name = "name_of_course", nullable = false)
    private String courseName;

    @Basic(fetch=FetchType.LAZY)
    @Column(name = "course_photo", nullable = false)
    private byte[] coursePhoto;

    @Column(name = "date_of_creation")
    private LocalDateTime creationDate;

    @PrePersist
    private void init() {
        creationDate = LocalDateTime.now();
    }
}