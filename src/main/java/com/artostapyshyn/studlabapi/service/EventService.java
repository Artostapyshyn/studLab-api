package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.dto.EventDto;
import com.artostapyshyn.studlabapi.entity.Event;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EventService {
    List<EventDto> findAll();

    List<EventDto> findUpcomingEvents(Pageable pageable);

    Optional<Event> findEventById(Long id);

    Optional<EventDto> findEventDtoById(Long id);

    Event save(Event event);

    int getCreatedEventCount();

    EventDto convertToDTO(Event event);

    List<EventDto> getRecommendedEvents(Long studentId, Pageable pageable);

    List<EventDto> findPopularEvents(Pageable pageable);

    List<EventDto> findAllEventsByDateAsc(Pageable pageable);

    List<EventDto> findAllEventsByCreationDateAsc(Pageable pageable);

    void deleteById(Long id);

    void updateEvent(Event existingEvent, Event updatedEvent);
}
