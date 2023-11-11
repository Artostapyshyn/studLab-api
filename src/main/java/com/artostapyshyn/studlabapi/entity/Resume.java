package com.artostapyshyn.studlabapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
@Entity
@Table(name = "resumes", indexes = {
        @Index(name = "idx_student_resumes", columnList = "student_id")
})
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "data", nullable = false)
    private byte[] data;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resume resume)) return false;
        return Objects.equals(getId(), resume.getId()) && Objects.equals(getName(), resume.getName()) && Arrays.equals(getData(), resume.getData()) && Objects.equals(getStudentId(), resume.getStudentId());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getId(), getName(), getStudentId());
        result = 31 * result + Arrays.hashCode(getData());
        return result;
    }
}