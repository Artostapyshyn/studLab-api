package com.artostapyshyn.studlabapi.entity;

import com.artostapyshyn.studlabapi.enums.RequestStatus;
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
@Table(name = "friend_requests", indexes = {
        @Index(name = "idx_sender", columnList = "sender_id"),
        @Index(name = "idx_receiver", columnList = "receiver_id")
})
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Student sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Student receiver;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RequestStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FriendRequest that)) return false;
        return Objects.equals(getId(), that.getId()) && getStatus() == that.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getStatus());
    }
}