package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.entity.FavouriteEvent;
import com.artostapyshyn.studlabapi.repository.EventRepository;
import com.artostapyshyn.studlabapi.repository.FavouriteEventRepository;
import com.artostapyshyn.studlabapi.service.EventService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final FavouriteEventRepository favouriteEventRepository;

    private AtomicInteger createdEventCount;

    @Override
    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    @Override
    public Optional<Event> findEventById(Long id) {
        return eventRepository.findEventById(id);
    }

    @Transactional
    @Override
    public Event save(Event event) {
        createdEventCount.incrementAndGet();
        return eventRepository.save(event);
    }

    public int getCreatedEventCount() {
        return createdEventCount.get();
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

    @Transactional
    @Override
    public void deleteById(Long id) {
        List<FavouriteEvent> favoriteEvents = favouriteEventRepository.findByEventId(id);
        favouriteEventRepository.deleteAll(favoriteEvents);
        eventRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void updateEvent(Event existingEvent, Event updatedEvent) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(updatedEvent, existingEvent);
    }
}
