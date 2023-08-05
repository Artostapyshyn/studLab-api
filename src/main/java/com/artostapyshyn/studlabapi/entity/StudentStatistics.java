package com.artostapyshyn.studlabapi.entity;

import com.artostapyshyn.studlabapi.enums.Interval;
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
@Entity
@Table(name = "student_statistics")
public class StudentStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_interval")
    private Interval interval;

    @Column(name = "count")
    private int count;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentStatistics that)) return false;
        return getCount() == that.getCount()
                && Objects.equals(getId(), that.getId())
                && getInterval() == that.getInterval();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getInterval(), getCount());
    }
}