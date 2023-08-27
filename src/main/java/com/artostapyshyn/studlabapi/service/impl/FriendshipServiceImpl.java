package com.artostapyshyn.studlabapi.service.impl;

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

    @Override
    public List<Friendship> findAllByStudentId(Long studentId) {
        return friendshipRepository.findAllByStudentId(studentId);
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
