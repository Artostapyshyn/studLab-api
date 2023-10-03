package com.artostapyshyn.studlabapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
@Entity
@Table(name = "student_friendships", indexes = {
        @Index(name = "idx_friendship_student_id", columnList = "student_id"),
        @Index(name = "idx_friend_id", columnList = "friend_id")
        }, uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "friend_id"}))
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private Student friend;
}