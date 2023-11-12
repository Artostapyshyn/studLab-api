package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Meeting;
import com.artostapyshyn.studlabapi.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.artostapyshyn.studlabapi.enums.MeetingType.PERSONAL;
import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomStudent;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class MeetingRepositoryTest {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Meeting createAndPersistMeetingWithParticipants() {
        Student author = createRandomStudent();
        entityManager.persist(author);

        Meeting meeting = new Meeting();
        meeting.setAuthor(author);
        meeting.setDescription("Test");
        meeting.setMeetingType(PERSONAL);
        meeting.setName("Test");
        meeting.setVenue("Test");
        meeting.setParticipants(Set.of(createRandomStudent(), createRandomStudent(), createRandomStudent()));
        meeting.getParticipants().forEach(entityManager::persist);
        meeting.setDate(LocalDateTime.now());
        entityManager.persist(meeting);
        entityManager.flush();
        entityManager.clear();
        return meeting;
    }

    @Test
    void findMeetingById() {
        Meeting meeting = createAndPersistMeetingWithParticipants();
        Meeting found = meetingRepository.findMeetingById(meeting.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(meeting.getId(), found.getId());
    }

    @Test
    void findAllByAuthorId() {
        Meeting meeting = createAndPersistMeetingWithParticipants();
        List<Meeting> found = meetingRepository.findAllByAuthorId(meeting.getAuthor().getId());
        assertEquals(1, found.size());
        assertEquals(meeting.getId(), found.get(0).getId());
    }

    @Test
    void findAllByParticipantsId() {
        Meeting meeting = createAndPersistMeetingWithParticipants();
        Student participant = meeting.getParticipants().iterator().next();
        List<Meeting> found = meetingRepository.findAllByParticipantsId(participant.getId());
        assertEquals(1, found.size());
        assertEquals(meeting.getId(), found.get(0).getId());
    }
}
