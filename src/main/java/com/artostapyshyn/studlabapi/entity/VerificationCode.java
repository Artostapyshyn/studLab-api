package com.artostapyshyn.studlabapi.entity;

import com.artostapyshyn.studlabapi.util.CustomLocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
@Table(name = "verification_codes", indexes = {
        @Index(name = "idx_student_codes", columnList = "student_id")
})
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
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime expirationDate;

    @Column(name = "last_sent_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime lastSentTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VerificationCode that)) return false;
        return getCode() == that.getCode()
                && Objects.equals(getId(), that.getId())
                && Objects.equals(getStudentId(), that.getStudentId())
                && Objects.equals(getEmail(), that.getEmail())
                && Objects.equals(getExpirationDate(), that.getExpirationDate())
                && Objects.equals(getLastSentTime(), that.getLastSentTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCode(), getStudentId(), getEmail(), getExpirationDate(), getLastSentTime());
    }

    @Override
    public String toString() {
        return "VerificationCode{" +
                "id=" + id +
                ", code=" + code +
                ", studentId=" + studentId +
                ", email='" + email + '\'' +
                ", expirationDate=" + expirationDate +
                ", lastSentTime=" + lastSentTime +
                '}';
    }
}
