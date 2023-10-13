package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.dto.EventDto;
import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomEvent;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void findAll() {
        List<Event> expectedEvents = List.of(
                createRandomEvent(),
                createRandomEvent(),
                createRandomEvent()
        );
        when(eventRepository.findAll()).thenReturn(expectedEvents);

        List<EventDto> actualEvents = eventService.findAll();

        assertEquals(expectedEvents.size(), actualEvents.size());
        assertTrue(actualEvents.containsAll(expectedEvents));
    }

    @Test
    void findEventById() {
        Long eventId = 123L;
        Event expectedEvent = createRandomEvent();
        when(eventRepository.findEventById(eventId)).thenReturn(Optional.of(expectedEvent));

        Optional<Event> actualEvent = eventService.findEventById(eventId);

        assertTrue(actualEvent.isPresent());
        assertEquals(expectedEvent, actualEvent.get());
    }

    @Test
    void save() {
        Event event = createRandomEvent();
        when(eventRepository.save(event)).thenReturn(event);

        Event savedEvent = eventService.save(event);

        assertNotNull(savedEvent);
        assertEquals(event.getId(), savedEvent.getId());
    }

    @Test
    void findPopularEvents() {
        List<Event> expectedPopularEvents = List.of(
                createRandomEvent(),
                createRandomEvent()
        );
        when(eventRepository.findPopularEvents(Pageable.unpaged())).thenReturn(expectedPopularEvents);

        List<EventDto> actualPopularEvents = eventService.findPopularEvents(Pageable.unpaged());

        assertEquals(expectedPopularEvents.size(), actualPopularEvents.size());
        assertTrue(actualPopularEvents.containsAll(expectedPopularEvents));
    }

    @Test
    void findAllEventsByDateDesc() {
        List<Event> expectedEventsByDateDesc = List.of(
                createRandomEvent(),
                createRandomEvent(),
                createRandomEvent()
        );
        when(eventRepository.findAllEventsByDateAsc(Pageable.unpaged())).thenReturn((Page<Event>) expectedEventsByDateDesc);

        List<EventDto> actualEventsByDateDesc = eventService.findAllEventsByDateAsc(Pageable.unpaged());

        assertEquals(expectedEventsByDateDesc.size(), actualEventsByDateDesc.size());
        assertTrue(actualEventsByDateDesc.containsAll(expectedEventsByDateDesc));
    }

    @Test
    void findAllEventsByCreationDateDesc() {
        List<Event> expectedEventsByCreationDateDesc = List.of(
                createRandomEvent(),
                createRandomEvent(),
                createRandomEvent()
        );
        when(eventRepository.findAllEventsByCreationDateAsc(Pageable.unpaged())).thenReturn((Page<Event>) expectedEventsByCreationDateDesc);

        List<EventDto> actualEventsByCreationDateDesc = eventService.findAllEventsByCreationDateAsc(Pageable.unpaged());

        assertEquals(expectedEventsByCreationDateDesc.size(), actualEventsByCreationDateDesc.size());
        assertTrue(actualEventsByCreationDateDesc.containsAll(expectedEventsByCreationDateDesc));
    }

    @Test
    void deleteById() {
        Long eventId = 123L;
        eventService.deleteById(eventId);

        verify(eventRepository, times(1)).deleteById(eventId);
    }

    @Test
    void updateEvent() {
        Event existingEvent = createRandomEvent();
        existingEvent.setVenue("Updated Venue");
        existingEvent.setNameOfEvent("Updated name");

        eventService.updateEvent(existingEvent, existingEvent);

        assertEquals(existingEvent.getVenue(), existingEvent.getVenue());
        assertEquals(existingEvent.getDate(), existingEvent.getDate());
    }
}