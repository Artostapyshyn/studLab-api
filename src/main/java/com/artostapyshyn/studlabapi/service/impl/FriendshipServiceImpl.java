package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.dto.FriendshipDTO;
import com.artostapyshyn.studlabapi.entity.Friendship;
import com.artostapyshyn.studlabapi.repository.FriendshipRepository;
import com.artostapyshyn.studlabapi.service.FriendshipService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipRepository friendshipRepository;

    public List<FriendshipDTO> findAllByStudentId(Long studentId) {
        List<Friendship> friendships = friendshipRepository.findAllByStudentId(studentId);
        return friendships.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private FriendshipDTO convertToDTO(Friendship friendship) {
        FriendshipDTO dto = new FriendshipDTO();
        dto.setId(friendship.getId());
        dto.setStudentId(friendship.getStudent().getId());
        dto.setFriendId(friendship.getFriend().getId());
        dto.setFriendFirstName(friendship.getFriend().getFirstName());
        dto.setFriendLastName(friendship.getFriend().getLastName());
        dto.setFriendPhoto(friendship.getFriend().getPhotoBytes());
        return dto;
    }


    @Override
    public Optional<Friendship> findFriendshipByStudentId(Long studentId) {
        return friendshipRepository.findFriendshipByStudentId(studentId);
    }

    @Override
    public Friendship save(Friendship friendship) {
        return friendshipRepository.save(friendship);
    }

    @Override
    @Transactional
    public void deleteByFriendId(Long id) {
        friendshipRepository.deleteByFriendId(id);
    }
}
