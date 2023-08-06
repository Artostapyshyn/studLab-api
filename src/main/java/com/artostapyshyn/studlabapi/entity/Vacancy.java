package com.artostapyshyn.studlabapi.entity;

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
@Cacheable
@Entity
@Table(name = "vacancies")
public class Vacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vacancy_id", nullable = false)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "name_of_vacancy", nullable = false)
    private String nameOfVacancy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vacancy vacancy)) return false;
        return Objects.equals(getId(), vacancy.getId())
                && Objects.equals(getDescription(), vacancy.getDescription())
                && Objects.equals(getNameOfVacancy(), vacancy.getNameOfVacancy());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDescription(), getNameOfVacancy());
    }

    @Override
    public String toString() {
        return "Vacancy{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", nameOfVacancy='" + nameOfVacancy + '\'' +
                '}';
    }
}