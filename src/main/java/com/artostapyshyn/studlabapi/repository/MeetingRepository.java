package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    Optional<Meeting> findMeetingById(Long id);

    List<Meeting> findAllByAuthorId(Long id);
}