package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.dto.EventDto;
import com.artostapyshyn.studlabapi.entity.Event;

import java.util.List;
import java.util.Optional;

public interface EventService {
    List<EventDto> findAll();

    Optional<Event> findEventById(Long id);

    Optional<EventDto> findEventDtoById(Long id);

    Event save(Event event);

    int getCreatedEventCount();

    EventDto convertToDTO(Event event);

    List<EventDto> findPopularEvents();

    List<EventDto> findAllEventsByDateDesc();

    List<EventDto> findAllEventsByCreationDateDesc();

    void deleteById(Long id);

    void updateEvent(Event existingEvent, Event updatedEvent);
}
