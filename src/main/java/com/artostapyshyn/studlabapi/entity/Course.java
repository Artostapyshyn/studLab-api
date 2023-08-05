package com.artostapyshyn.studlabapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

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

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "course_photo", nullable = false)
    private byte[] coursePhoto;

    @Column(name = "date_of_creation")
    @CreationTimestamp
    private LocalDateTime creationDate;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course course)) return false;
        return Objects.equals(getId(), course.getId())
                && Objects.equals(getCourseLink(), course.getCourseLink())
                && Objects.equals(getCourseDescription(), course.getCourseDescription())
                && Objects.equals(getCourseName(), course.getCourseName())
                && Arrays.equals(getCoursePhoto(), course.getCoursePhoto())
                && Objects.equals(getCreationDate(), course.getCreationDate());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getId(), getCourseLink(), getCourseDescription(), getCourseName(), getCreationDate());
        result = 31 * result + Arrays.hashCode(getCoursePhoto());
        return result;
    }
}