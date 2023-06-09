package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.repository.EventRepository;
import com.artostapyshyn.studlabapi.service.EventService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private AtomicInteger createdEventCount;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        this.createdEventCount = new AtomicInteger(0);
    }

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
    @Cacheable(value = "eventsByDateDesc", key = "'eventsByDateDesc'")
    public List<Event> findAllEventsByDateDesc() {
        return eventRepository.findAllEventsByDateDesc();
    }

    @Override
    @Cacheable(value = "eventsByCreationDateDesc", key = "'eventsByCreationDateDesc'")
    public List<Event> findAllEventsByCreationDateDesc() {
        return eventRepository.findAllEventsByCreationDateDesc();
    }

    @Transactional
    @Override
    @CacheEvict(value = {"eventsByDateDesc", "eventsByCreationDateDesc"}, allEntries = true)
    public void deleteById(Long id) {
        eventRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void updateEvent(Event existingEvent, Event updatedEvent) {
        Optional.ofNullable(updatedEvent.getVenue()).ifPresent(existingEvent::setVenue);
        Optional.ofNullable(updatedEvent.getDate()).ifPresent(existingEvent::setDate);
        Optional.ofNullable(updatedEvent.getDescription()).ifPresent(existingEvent::setDescription);
        Optional.ofNullable(updatedEvent.getNameOfEvent()).ifPresent(existingEvent::setNameOfEvent);
        Optional.ofNullable(updatedEvent.getEventPhoto()).ifPresent(existingEvent::setEventPhoto);
        Optional.ofNullable(updatedEvent.getEventType()).ifPresent(existingEvent::setEventType);
    }
}
