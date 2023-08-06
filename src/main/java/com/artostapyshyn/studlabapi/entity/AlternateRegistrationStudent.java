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
@Table(name = "alternate_registration_student")
public class AlternateRegistrationStudent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlternateRegistrationStudent that)) return false;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getLastName(), that.getLastName())
                && Objects.equals(getFirstName(), that.getFirstName())
                && Objects.equals(getCode(), that.getCode())
                && Objects.equals(getEmail(), that.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getLastName(), getFirstName(), getCode(), getEmail());
    }

    @Override
    public String toString() {
        return "AlternateRegistrationStudent{" +
                "id=" + id +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", code='" + code + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}