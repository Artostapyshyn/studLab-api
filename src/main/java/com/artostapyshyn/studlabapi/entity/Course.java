package com.artostapyshyn.studlabapi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
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
                && Objects.equals(getCreationDate(), course.getCreationDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCourseLink(), getCourseDescription(), getCourseName(), getCreationDate());
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", courseLink='" + courseLink + '\'' +
                ", courseDescription='" + courseDescription + '\'' +
                ", courseName='" + courseName + '\'' +
                ", coursePhoto=" + Arrays.toString(coursePhoto) +
                ", creationDate=" + creationDate +
                '}';
    }
}