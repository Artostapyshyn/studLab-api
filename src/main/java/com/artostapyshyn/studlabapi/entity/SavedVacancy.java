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
@Table(name = "saved_vacancies")
public class SavedVacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @ManyToOne
    @JoinColumn(name = "vacancy_id")
    private Vacancy vacancy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SavedVacancy that)) return false;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getStudentId(), that.getStudentId())
                && Objects.equals(getVacancy(), that.getVacancy());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getStudentId(), getVacancy());
    }

    @Override
    public String toString() {
        return "SavedVacancy{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", vacancy=" + vacancy +
                '}';
    }
}