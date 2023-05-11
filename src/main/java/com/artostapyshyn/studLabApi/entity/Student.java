package com.artostapyshyn.studLabApi.entity;

import com.artostapyshyn.studLabApi.enums.Role;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @Lob
    @Column(name = "student_photo")
    private byte[] photo;

    @Column(name = "student_photo_filename")
    private String photoFilename;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "student_resumes")
    @ElementCollection
    private Set<byte[]> resumes;

    @Column(name = "student_resume_filenames")
    @ElementCollection
    private Set<String> resumeFilenames;

    @Column(name = "student_certificates")
    @ElementCollection
    private Set<byte[]> certificates;

    @Column(name = "student_certificates_filenames")
    @ElementCollection
    private Set<String> certificatesFilenames;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id")
    @JsonBackReference
    private University university;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;
}