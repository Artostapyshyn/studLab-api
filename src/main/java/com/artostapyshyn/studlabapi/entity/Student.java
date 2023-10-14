package com.artostapyshyn.studlabapi.entity;

import com.artostapyshyn.studlabapi.enums.AuthStatus;
import com.artostapyshyn.studlabapi.enums.Role;
import com.artostapyshyn.studlabapi.util.CustomLocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
@Entity
@Table(name = "students", indexes = {
        @Index(name = "idx_student_university", columnList = "university_id")
})
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "birth_date")
    private String birthDate;

    @Column(name = "major")
    private String major;

    @Column(name = "course")
    private String course;

    @Column(name = "student_city")
    private String city;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "student_photo")
    private byte[] photoBytes;

    @Email
    @Column(name = "email", unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password")
    private String password;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "has_new_messages")
    private Boolean hasNewMessages;

    @Column(name = "blocked_until")
    private LocalDateTime blockedUntil;

    @Column(name = "registration_date")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime registrationDate;

    @JsonIgnore
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "student"})
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private Set<Friendship> friendships = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    private University university;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_status")
    private AuthStatus authStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @JsonProperty("university")
    public Map<String, Object> getUniversityData() {
        Map<String, Object> universityData = new HashMap<>();
        universityData.put("id", university.getId());
        universityData.put("name", university.getName());
        return universityData;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student student)) return false;
        return isEnabled() == student.isEnabled()
                && Objects.equals(getId(), student.getId())
                && Objects.equals(getLastName(), student.getLastName())
                && Objects.equals(getFirstName(), student.getFirstName())
                && Objects.equals(getBirthDate(), student.getBirthDate())
                && Objects.equals(getMajor(), student.getMajor())
                && Objects.equals(getCourse(), student.getCourse())
                && Objects.equals(getCity(), student.getCity())
                && Objects.equals(getEmail(), student.getEmail())
                && Objects.equals(getPassword(), student.getPassword())
                && Objects.equals(getHasNewMessages(), student.getHasNewMessages())
                && Objects.equals(getBlockedUntil(), student.getBlockedUntil())
                && Objects.equals(getRegistrationDate(), student.getRegistrationDate())
                && Objects.equals(getUniversity(), student.getUniversity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getLastName(), getFirstName(), getBirthDate(), getMajor(), getCourse(), getCity(), getEmail(), getPassword(), isEnabled(), getHasNewMessages(), getBlockedUntil(), getRegistrationDate(), getUniversity());
    }
}