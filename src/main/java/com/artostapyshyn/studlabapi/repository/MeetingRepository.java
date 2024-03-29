package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    Optional<Meeting> findMeetingById(Long id);

    List<Meeting> findAllByAuthorId(Long id);

    @Query("SELECT m FROM Meeting m JOIN m.author a JOIN a.friendships f WHERE a.id = :studentId")
    Set<Meeting> findMeetingsByStudentFriends(@Param("studentId") Long studentId);

    List<Meeting> findAllByParticipantsId(Long id);
}