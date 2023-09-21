package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.dto.FriendRequestDto;
import com.artostapyshyn.studlabapi.entity.FriendRequest;
import com.artostapyshyn.studlabapi.entity.Friendship;
import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.enums.RequestStatus;
import com.artostapyshyn.studlabapi.exception.exceptions.ResourceNotFoundException;
import com.artostapyshyn.studlabapi.repository.FriendRequestRepository;
import com.artostapyshyn.studlabapi.repository.FriendshipRepository;
import com.artostapyshyn.studlabapi.repository.StudentRepository;
import com.artostapyshyn.studlabapi.service.FriendRequestService;
import com.artostapyshyn.studlabapi.service.MessageService;
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

    private final MessageService messageService;

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
        messageService.addMessageToStudent(receiverId, "У вас нові сповіщення у профілі.");
        messageService.updateNewMessageStatus(receiverId, true);
        return friendRequestRepository.save(request);
    }

    @Override
    public Optional<FriendRequest> findBySenderIdAndReceiverId(Long senderId, Long receiverId) {
        return friendRequestRepository.findBySenderIdAndReceiverId(senderId, receiverId);
    }

    @Override
    public List<FriendRequestDto> getReceivedFriendRequests(Long studentId) {
         List<FriendRequest> friendRequests = friendRequestRepository.findAllByReceiverIdAndStatus(studentId, RequestStatus.PENDING);
        return friendRequests.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private FriendRequestDto convertToDTO(FriendRequest friendRequest) {
        FriendRequestDto dto = new FriendRequestDto();
        dto.setId(friendRequest.getId());
        dto.setReceiverId(friendRequest.getReceiver().getId());
        dto.setSenderId(friendRequest.getSender().getId());
        dto.setSenderFirstName(friendRequest.getSender().getFirstName());
        dto.setSenderLastName(friendRequest.getSender().getLastName());
        dto.setSenderPhoto(friendRequest.getSender().getPhotoBytes());
        return dto;
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

        Friendship secondFriendship = new Friendship();
        secondFriendship.setStudent(request.getReceiver());
        secondFriendship.setFriend(request.getSender());
        friendshipRepository.save(secondFriendship);
    }

    @Override
    @Transactional
    public void declineFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found!"));

        request.setStatus(RequestStatus.DECLINED);
        friendRequestRepository.save(request);

        Optional<Friendship> friendship = friendshipRepository.findFriendshipByStudentId(request.getSender().getId());
        if(friendship.isPresent()){
           throw new IllegalArgumentException("Friendship found!");
        }
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

    @Override
    public boolean isSentRequest(Long studentId, Long receiverId) {
        return friendRequestRepository.existsBySenderIdAndReceiverId(studentId, receiverId);
    }
}
