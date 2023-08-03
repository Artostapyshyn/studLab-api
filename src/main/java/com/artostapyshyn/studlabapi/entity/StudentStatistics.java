package com.artostapyshyn.studlabapi.entity;

import com.artostapyshyn.studlabapi.enums.Interval;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}