package com.artostapyshyn.studlabapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
@Entity
@Table(name = "interests")
public class Interest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "interested_students",
            joinColumns = @JoinColumn(name = "interest_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Student> interestedStudents = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Interest interest)) return false;
        return Objects.equals(getId(), interest.getId()) && Objects.equals(getName(), interest.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}