package com.artostapyshyn.studlabapi.entity;

import com.artostapyshyn.studlabapi.util.CustomLocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
@Entity
@Table(name = "universities")
public class University {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "domain", nullable = false)
    private String domain;

    @Column(name = "registration_date")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime registrationDate;

    @JsonBackReference
    @OneToMany(mappedBy = "university", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Student> students;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof University that)) return false;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getName(), that.getName())
                && Objects.equals(getDomain(), that.getDomain());
    }

    @Override
    public String toString() {
        return "University{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", domain='" + domain + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDomain());
    }
}
