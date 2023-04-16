package com.artostapyshyn.studLabApi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "verification_codes")
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int code;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "student_email")
    private String email;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;
}
