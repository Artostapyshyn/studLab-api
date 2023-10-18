package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Meeting;
import com.artostapyshyn.studlabapi.repository.MeetingRepository;
import com.artostapyshyn.studlabapi.service.MeetingService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;

    private final ModelMapper modelMapper;

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
    public List<Meeting> findAllByAuthorId(Long id) {
        return meetingRepository.findAllByAuthorId(id);
    }

    @Override
    public void updateMeeting(Meeting existingMeeting, Meeting updatedMeeting) {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(updatedMeeting, existingMeeting);
        meetingRepository.save(existingMeeting);
    }

    @Override
    public List<Meeting> findAll() {
        return meetingRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        meetingRepository.deleteById(id);
    }
}
