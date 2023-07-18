package com.artostapyshyn.studlabapi.entity;

import com.artostapyshyn.studlabapi.enums.Role;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
@Entity
@Table(name = "students")
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

    @Basic(fetch=FetchType.LAZY)
    @Column(name = "student_photo")
    private byte[] photoBytes;

    @Column(name = "email", unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password")
    private String password;

    @Column(name = "student_resumes")
    @ElementCollection
    @CollectionTable(name = "student_resumes")
    private Set<String> resumes = new HashSet<>();

    @Column(name = "student_resume_filenames")
    @ElementCollection
    @CollectionTable(name = "student_resume_filenames")
    private Set<String> resumeFilenames = new HashSet<>();

    @Column(name = "student_certificates")
    @ElementCollection
    @CollectionTable(name = "student_certificates")
    private Set<String> certificates = new HashSet<>();

    @Column(name = "student_certificates_filenames")
    @ElementCollection
    @CollectionTable(name = "student_certificates_filenames")
    private Set<String> certificatesFilenames = new HashSet<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "has_new_messages")
    private Boolean hasNewMessages;

    @Column(name = "blocked_until")
    private LocalDateTime blockedUntil;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @JsonIgnore
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "student"})
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;

    @ManyToOne(fetch = FetchType.LAZY)
    private University university;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student student)) return false;
        return Objects.equals(getId(), student.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}