package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.dto.FriendRequestDto;
import com.artostapyshyn.studlabapi.entity.FriendRequest;

import java.util.List;
import java.util.Optional;

public interface FriendRequestService {

    void declineFriendRequest(Long requestId);

    List<FriendRequest> getAllFriendRequests(Long studentId);

    void acceptFriendRequest(Long requestId);

    FriendRequest sendFriendRequest(Long senderId, Long receiverId);

    Optional<FriendRequest> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    List<FriendRequestDto> getReceivedFriendRequests(Long studentId);

    void deleteUnusedRequests();

    FriendRequest save(FriendRequest friendRequest);

    Optional<FriendRequest> findById(Long id);
}
