package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.dto.FriendshipDTO;
import com.artostapyshyn.studlabapi.entity.Friendship;

import java.util.List;
import java.util.Optional;

public interface FriendshipService {

    List<FriendshipDTO> findAllByStudentId(Long studentId);

    Optional<Friendship> findFriendshipByStudentId(Long studentId);

    Friendship save(Friendship friendship);

    void deleteByFriendId(Long id);
}
