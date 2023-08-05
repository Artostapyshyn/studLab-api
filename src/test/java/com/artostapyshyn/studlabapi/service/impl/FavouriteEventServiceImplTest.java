package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.entity.FavouriteEvent;
import com.artostapyshyn.studlabapi.repository.EventRepository;
import com.artostapyshyn.studlabapi.repository.FavouriteEventRepository;
import org.junit.jupiter.api.Test;

import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomEvent;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
class FavouriteEventServiceImplTest {

    @Mock
    private FavouriteEventRepository favouriteEventRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private FavouriteEventServiceImpl favouriteEventService;

    @Test
    void findByStudentId() {
        Long studentId = 1L;
        List<FavouriteEvent> expectedList = List.of(new FavouriteEvent(), new FavouriteEvent());

        when(favouriteEventRepository.findByStudentId(studentId)).thenReturn(expectedList);

        List<FavouriteEvent> resultList = favouriteEventService.findByStudentId(studentId);

        assertEquals(expectedList, resultList);
        verify(favouriteEventRepository, times(1)).findByStudentId(studentId);
    }

    @Test
    void findByStudentIdAndEventId() {
        Long studentId = 1L;
        Long eventId = 2L;
        FavouriteEvent expectedFavouriteEvent = new FavouriteEvent();

        when(favouriteEventRepository.findByStudentIdAndEventId(studentId, eventId))
                .thenReturn(expectedFavouriteEvent);

        Optional<FavouriteEvent> resultOptional = favouriteEventService.findByStudentIdAndEventId(studentId, eventId);
        FavouriteEvent resultFavouriteEvent = resultOptional.orElse(null);

        assertEquals(expectedFavouriteEvent, resultFavouriteEvent);
        verify(favouriteEventRepository, times(1)).findByStudentIdAndEventId(studentId, eventId);
    }

    @Test
    void save() {
        FavouriteEvent favouriteEvent = new FavouriteEvent();

        when(favouriteEventRepository.save(favouriteEvent)).thenReturn(favouriteEvent);

        FavouriteEvent savedFavouriteEvent = favouriteEventService.save(favouriteEvent);

        assertEquals(favouriteEvent, savedFavouriteEvent);
        verify(favouriteEventRepository, times(1)).save(favouriteEvent);
    }

    @Test
    void delete() {
        FavouriteEvent favouriteEvent = new FavouriteEvent();

        favouriteEventService.delete(favouriteEvent);

        verify(favouriteEventRepository, times(1)).delete(favouriteEvent);
    }

    @Test
    void removeFromFavorites() {
        Long eventId = 1L;
        Event event = createRandomEvent();
        event.setId(eventId);
        event.setFavoriteCount(5);

        when(eventRepository.findEventById(eventId)).thenReturn(Optional.of(event));

        favouriteEventService.removeFromFavorites(eventId);

        verify(eventRepository, times(1)).findEventById(eventId);
        verify(eventRepository, times(1)).save(event);
        assertEquals(4, event.getFavoriteCount());
    }

    @Test
    void addToFavorites() {
        Long eventId = 1L;
        Event event = createRandomEvent();
        event.setId(eventId);
        event.setFavoriteCount(5);

        when(eventRepository.findEventById(eventId)).thenReturn(Optional.of(event));

        favouriteEventService.addToFavorites(eventId);

        verify(eventRepository, times(1)).findEventById(eventId);
        verify(eventRepository, times(1)).save(event);
        assertEquals(6, event.getFavoriteCount());
    }
}