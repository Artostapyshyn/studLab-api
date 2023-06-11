package com.artostapyshyn.studlabapi.entity;

import com.artostapyshyn.studlabapi.enums.ComplaintType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "complaints")
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "studentId")
    private Long studentId;

    @Column(name = "commentId")
    private Long commentId;

    @Column(name = "status")
    private String status;

    @Column(name = "complaint_reason")
    private  String complaintReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ComplaintType type;
}