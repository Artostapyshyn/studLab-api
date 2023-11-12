package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("SELECT f FROM Friendship f where f.student.id = :studentId and f.friend.id = :friendId")
    List<Friendship> findAllByStudentId(@Param("studentId") Long id);

    Optional<Friendship> findFriendshipByStudentId(Long studentId);

    void deleteByFriendId(Long friendId);
}