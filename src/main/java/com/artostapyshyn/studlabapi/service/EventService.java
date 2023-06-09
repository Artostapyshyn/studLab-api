package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.Event;

import java.util.List;
import java.util.Optional;

public interface EventService {
    List<Event> findAll();

    Optional<Event> findEventById(Long id);

    Event save(Event event);

    int getCreatedEventCount();

    List<Event> findPopularEvents();

    List<Event> findAllEventsByDateDesc();

    List<Event> findAllEventsByCreationDateDesc();

    void deleteById(Long id);

    void updateEvent(Event existingEvent, Event updatedEvent);
}
