package com.artostapyshyn.studlabapi.entity;

import com.artostapyshyn.studlabapi.enums.Role;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    @Min(1)
    @Max(5)
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

    @ManyToOne(fetch = FetchType.EAGER)
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
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", photoBytes=" + Arrays.toString(photoBytes) +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", university=" + university +
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
                && Arrays.equals(getPhotoBytes(), student.getPhotoBytes())
                && Objects.equals(getEmail(), student.getEmail())
                && Objects.equals(getPassword(), student.getPassword())
                && Objects.equals(getResumes(), student.getResumes())
                && Objects.equals(getResumeFilenames(), student.getResumeFilenames())
                && Objects.equals(getCertificates(), student.getCertificates())
                && Objects.equals(getCertificatesFilenames(), student.getCertificatesFilenames())
                && Objects.equals(getMessages(), student.getMessages())
                && Objects.equals(getHasNewMessages(), student.getHasNewMessages())
                && Objects.equals(getBlockedUntil(), student.getBlockedUntil())
                && Objects.equals(getRegistrationDate(), student.getRegistrationDate())
                && Objects.equals(getComments(), student.getComments())
                && Objects.equals(getUniversity(), student.getUniversity())
                && getRole() == student.getRole();
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getId(), getLastName(), getFirstName(), getBirthDate(), getMajor(), getCourse(), getCity(), getEmail(), getPassword(), getResumes(), getResumeFilenames(), getCertificates(), getCertificatesFilenames(), getMessages(), isEnabled(), getHasNewMessages(), getBlockedUntil(), getRegistrationDate(), getComments(), getUniversity(), getRole());
        result = 31 * result + Arrays.hashCode(getPhotoBytes());
        return result;
    }
}