package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.FriendRequest;
import com.artostapyshyn.studlabapi.entity.Friendship;
import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.enums.RequestStatus;
import com.artostapyshyn.studlabapi.exception.exceptions.ResourceNotFoundException;
import com.artostapyshyn.studlabapi.repository.FriendRequestRepository;
import com.artostapyshyn.studlabapi.repository.FriendshipRepository;
import com.artostapyshyn.studlabapi.repository.StudentRepository;
import com.artostapyshyn.studlabapi.service.FriendRequestService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;

    private final FriendshipRepository friendshipRepository;

    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public FriendRequest sendFriendRequest(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Sender and Receiver cannot be the same person!");
        }

        Optional<FriendRequest> existingRequest = friendRequestRepository.findBySenderIdAndReceiverId(senderId, receiverId);
        if (existingRequest.isPresent()) {
            throw new IllegalArgumentException("Friend request already exists between these users!");
        }

        Student sender = studentRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found!"));

        Student receiver = studentRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found!"));

        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);

        return friendRequestRepository.save(request);
    }

    @Override
    public Optional<FriendRequest> findBySenderIdAndReceiverId(Long senderId, Long receiverId) {
        return friendRequestRepository.findBySenderIdAndReceiverId(senderId, receiverId);
    }

    @Override
    public List<FriendRequest> getReceivedFriendRequests(Long studentId) {
        return friendRequestRepository.findAllByReceiverIdAndStatus(studentId, RequestStatus.PENDING);
    }

    @Override
    public FriendRequest save(FriendRequest friendRequest) {
        return friendRequestRepository.save(friendRequest);
    }

    @Override
    public Optional<FriendRequest> findById(Long id) {
        return friendRequestRepository.findById(id);
    }

    @Override
    @Transactional
    public void acceptFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found!"));

        request.setStatus(RequestStatus.ACCEPTED);
        friendRequestRepository.save(request);

        Friendship friendship = new Friendship();
        friendship.setStudent(request.getSender());
        friendship.setFriend(request.getReceiver());
        friendshipRepository.save(friendship);
    }

    @Override
    @Transactional
    public void declineFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found!"));

        request.setStatus(RequestStatus.DECLINED);
        friendRequestRepository.save(request);

        Friendship friendship = friendshipRepository.findFriendshipByStudentId(request.getSender().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Friendship not found!"));
        friendRequestRepository.deleteById(friendship.getId());
    }

    @Override
    public List<FriendRequest> getAllFriendRequests(Long studentId) {
        return friendRequestRepository.findAllByReceiverId(studentId);
    }

    @Scheduled(fixedRate = 90000000)
    @Transactional
    @Override
    public void deleteUnusedRequests() {
        List<FriendRequest> toDelete = friendRequestRepository.findAllByStatusIn(Arrays.asList(RequestStatus.DECLINED, RequestStatus.ACCEPTED));
        friendRequestRepository.deleteAll(toDelete);
    }
}
