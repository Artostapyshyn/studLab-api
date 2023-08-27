package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.FriendRequest;
import com.artostapyshyn.studlabapi.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findAllByReceiverId(Long studentId);

    @Query("SELECT f FROM FriendRequest f WHERE f.receiver.id = :studentId AND f.status = :status")
    List<FriendRequest> findAllByReceiverIdAndStatus(@Param("studentId") Long studentId, @Param("status") RequestStatus status);

    List<FriendRequest> findAllByStatusIn(List<RequestStatus> statuses);

    Optional<FriendRequest> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
}