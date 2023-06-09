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
@Table(name = "complaints")
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "student_Id", nullable = false)
    private Long studentId;

    @Column(name = "comment_Id", nullable = false)
    private Long commentId;

    @Column(name = "status")
    private String status;

    @Column(name = "complaint_reason", nullable = false)
    private String complaintReason;

    @Column(name = "block_user")
    private boolean blockUser;

    @Column(name = "block_duration")
    private String blockDuration;

    @Column(name = "delete_comment")
    private boolean deleteComment;

    @Column(name = "close_complaint")
    private boolean closeComplaint;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Complaint complaint)) return false;
        return Objects.equals(getId(), complaint.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}