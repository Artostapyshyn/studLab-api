package com.artostapyshyn.studLabApi.service.impl;

import com.artostapyshyn.studLabApi.entity.Event;
import com.artostapyshyn.studLabApi.repository.EventRepository;
import com.artostapyshyn.studLabApi.service.EventService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    @Override
    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    @Override
    public Event findEventById(Long id) {
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
    public List<Event> findAllOrderByDateAsc() {
        return eventRepository.findAllOrderByDateAsc();
    }

    @Override
    public List<Event> findAllOrderByCreationDateAsc() {
        return eventRepository.findAllOrderByCreationDateAsc();
    }
}
