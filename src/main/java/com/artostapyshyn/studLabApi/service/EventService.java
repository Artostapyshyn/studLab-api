package com.artostapyshyn.studLabApi.service;

import com.artostapyshyn.studLabApi.entity.Event;

import java.util.List;

public interface EventService {
    List<Event> findAll();

    Event findEventById(Long id);

    Event save(Event event);
}
