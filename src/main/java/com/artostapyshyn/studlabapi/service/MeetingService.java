package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.Meeting;

import java.util.List;
import java.util.Optional;

public interface MeetingService {
    Meeting save(Meeting meeting);

    Optional<Meeting> findMeetingById(Long id);

    Optional<Meeting> findById(Long id);

    List<Meeting> findAllByAuthorId(Long id);

    void updateMeeting(Meeting existingMeeting, Meeting updatedMeeting);

    List<Meeting> findAll();

    void deleteById(Long id);
}
