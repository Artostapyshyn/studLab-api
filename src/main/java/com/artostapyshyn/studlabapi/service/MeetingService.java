package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.dto.MeetingDto;
import com.artostapyshyn.studlabapi.entity.Meeting;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MeetingService {
    Meeting save(Meeting meeting);

    Optional<Meeting> findMeetingById(Long id);

    Optional<Meeting> findById(Long id);

    List<MeetingDto> findAllByAuthorId(Long id);

    void updateMeeting(Meeting existingMeeting, Meeting updatedMeeting);

    Set<MeetingDto> findMeetingsByStudentFriends(Long studentId);

    List<MeetingDto> findAll();

    List<Meeting> findAllByParticipantsId(Long id);

    void deleteById(Long id);

    MeetingDto convertToDTO(Meeting meeting);
}
