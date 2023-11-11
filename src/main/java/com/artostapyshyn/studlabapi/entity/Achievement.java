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
@Table(name = "achievements",
        indexes = {
                @Index(name = "index_achievements_student_id", columnList = "student_id"),
        })
public class Achievement {
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
        if (!(o instanceof Achievement that)) return false;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName()) && Arrays.equals(getData(), that.getData());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getId(), getName());
        result = 31 * result + Arrays.hashCode(getData());
        return result;
    }
}