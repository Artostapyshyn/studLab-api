package com.artostapyshyn.studLabApi.service;

import com.artostapyshyn.studLabApi.entity.Event;

import java.util.List;
import java.util.Optional;

public interface EventService {
    List<Event> findAll();

    Optional<Event> findEventById(Long id);

    Event save(Event event);

    List<Event> findPopularEvents();

    List<Event> findAllEventsByDateDesc();

    List<Event> findAllEventsByCreationDateDesc();

    void deleteById(Long id);
}
