package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.dto.EventDto;
import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.entity.EventCounter;
import com.artostapyshyn.studlabapi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
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

    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        EventCounterRepository eventCounterRepository = mock(EventCounterRepository.class);
        FavouriteEventRepository favouriteEventRepository = mock(FavouriteEventRepository.class);
        TagRepository tagRepository = mock(TagRepository.class);
        StudentRepository studentRepository = mock(StudentRepository.class);

        eventService = new EventServiceImpl(eventRepository, favouriteEventRepository,
                eventCounterRepository, tagRepository, studentRepository, modelMapper);
    }

    @Test
    void findAll() {
        List<Event> expectedEvents = List.of(createRandomEvent(), createRandomEvent(), createRandomEvent());
        when(eventRepository.findAll()).thenReturn(expectedEvents);

        List<EventDto> actualEvents = eventService.findAll();

        List<EventDto> expectedDtos = expectedEvents.stream()
                .map(event -> modelMapper.map(event, EventDto.class))
                .toList();

        assertEquals(expectedDtos.size(), actualEvents.size());
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
        EventCounter counter = new EventCounter();
        counter.increment();
        when(eventRepository.save(event)).thenReturn(event);

        Event actualEvent = eventService.save(event);
        assertEquals(event, actualEvent);
    }

    @Test
    void deleteById() {
        Event event = createRandomEvent();
        when(eventRepository.save(event)).thenReturn(event);
        doNothing().when(eventRepository).deleteById(event.getId());

        eventService.deleteById(event.getId());
        verify(eventRepository, times(1)).deleteById(event.getId());
    }

    @Test
    void updateEvent() {
        Event event = createRandomEvent();
        when(eventRepository.save(event)).thenReturn(event);

        Event actualEvent = createRandomEvent();
        when(eventRepository.save(actualEvent)).thenReturn(actualEvent);
        eventService.updateEvent(event, actualEvent);
        assertEquals(event, actualEvent);
    }
}