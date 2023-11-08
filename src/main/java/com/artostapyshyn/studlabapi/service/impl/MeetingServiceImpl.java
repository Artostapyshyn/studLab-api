package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.dto.MeetingDto;
import com.artostapyshyn.studlabapi.entity.Meeting;
import com.artostapyshyn.studlabapi.repository.MeetingRepository;
import com.artostapyshyn.studlabapi.service.MeetingService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;

    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public Meeting save(Meeting meeting) {
        return meetingRepository.save(meeting);
    }

    @Override
    public Optional<Meeting> findMeetingById(Long id) {
        return meetingRepository.findMeetingById(id);
    }

    @Override
    public Optional<Meeting> findById(Long id) {
        return meetingRepository.findById(id);
    }

    @Override
    public List<MeetingDto> findAllByAuthorId(Long id) {
        List<Meeting> meetings = meetingRepository.findAllByAuthorId(id);
        return meetings.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional
    @Override
    public void updateMeeting(Meeting existingMeeting, Meeting updatedMeeting) {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(updatedMeeting, existingMeeting);
        meetingRepository.save(existingMeeting);
    }

    @Override
    public Set<MeetingDto> findMeetingsByStudentFriends(Long studentId) {
        Set<Meeting> meetings = meetingRepository.findMeetingsByStudentFriends(studentId);
        return meetings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toSet());
    }

    @Override
    public List<MeetingDto> findAll() {
        List<Meeting> meetings = meetingRepository.findAll();
        return meetings.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<Meeting> findAllByParticipantsId(Long id) {
        return meetingRepository.findAllByParticipantsId(id);
    }

    @Override
    public void deleteById(Long id) {
        meetingRepository.deleteById(id);
    }

    @Override
    public MeetingDto convertToDTO(Meeting meeting) {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        return modelMapper.map(meeting, MeetingDto.class);
    }
}
