package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.repository.EventRepository;
import com.artostapyshyn.studlabapi.service.EventService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    @Override
    public Optional<Event> findEventById(Long id) {
        return eventRepository.findEventById(id);
    }

    @Override
    public Event save(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public List<Event> findPopularEvents() {
        return eventRepository.findPopularEvents();
    }

    @Override
    public List<Event> findAllEventsByDateDesc() {
        return eventRepository.findAllEventsByDateDesc();
    }

    @Override
    public List<Event> findAllEventsByCreationDateDesc() {
        return eventRepository.findAllEventsByCreationDateDesc();
    }

    @Override
    public void deleteById(Long id) {
        eventRepository.deleteById(id);
    }

}