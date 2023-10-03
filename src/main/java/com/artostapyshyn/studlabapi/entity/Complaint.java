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
@Table(name = "complaints",
        indexes = {
                @Index(name = "index_complaints_student_id", columnList = "student_id"),
                @Index(name = "index_comment_id", columnList = "comment_id"),
        })
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "comment_id", nullable = false)
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
        return isBlockUser() == complaint.isBlockUser()
                && isDeleteComment() == complaint.isDeleteComment()
                && isCloseComplaint() == complaint.isCloseComplaint()
                && Objects.equals(getId(), complaint.getId())
                && Objects.equals(getStudentId(), complaint.getStudentId())
                && Objects.equals(getCommentId(), complaint.getCommentId())
                && Objects.equals(getStatus(), complaint.getStatus())
                && Objects.equals(getComplaintReason(), complaint.getComplaintReason());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getStudentId(), getCommentId(), getStatus(), getComplaintReason(), isBlockUser(), isDeleteComment(), isCloseComplaint());
    }

    @Override
    public String toString() {
        return "Complaint{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", commentId=" + commentId +
                ", status='" + status + '\'' +
                ", complaintReason='" + complaintReason + '\'' +
                ", blockUser=" + blockUser +
                ", blockDuration='" + blockDuration + '\'' +
                ", deleteComment=" + deleteComment +
                ", closeComplaint=" + closeComplaint +
                '}';
    }
}