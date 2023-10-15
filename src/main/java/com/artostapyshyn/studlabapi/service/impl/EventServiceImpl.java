package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.dto.EventDto;
import com.artostapyshyn.studlabapi.entity.*;
import com.artostapyshyn.studlabapi.repository.EventCounterRepository;
import com.artostapyshyn.studlabapi.repository.EventRepository;
import com.artostapyshyn.studlabapi.repository.FavouriteEventRepository;
import com.artostapyshyn.studlabapi.service.EventService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final FavouriteEventRepository favouriteEventRepository;

    private final EventCounterRepository eventCounterRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<EventDto> findAll() {
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<EventDto> findUpcomingEvents(Pageable pageable) {
        Page<Event> events = eventRepository.findUpcomingEvents(pageable);
        return events.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public Optional<Event> findEventById(Long id) {
        return eventRepository.findEventById(id);
    }

    @Override
    public Optional<EventDto> findEventDtoById(Long id) {
        Optional<Event> event = eventRepository.findById(id);
        return event.map(this::convertToDTO);
    }

    @Transactional
    @Override
    public Event save(Event event) {
        EventCounter counter = eventCounterRepository.findById(1L).orElse(new EventCounter());
        counter.increment();
        eventCounterRepository.save(counter);
        return eventRepository.save(event);
    }

    public int getCreatedEventCount() {
        return eventCounterRepository.findById(1L).map(EventCounter::getCounter).orElse(0);
    }

    @Override
    public List<EventDto> findPopularEvents(Pageable pageable) {
        List<Event> events = eventRepository.findPopularEvents(pageable);
        return events.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<EventDto> findAllEventsByDateAsc(Pageable pageable) {
        Page<Event> events = eventRepository.findAllEventsByDateAsc(pageable);
        return events.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<EventDto> findAllEventsByCreationDateAsc(Pageable pageable) {
        Page<Event> events = eventRepository.findAllEventsByCreationDateAsc(pageable);
        return events.stream()
                .map(this::convertToDTO)
                .toList();
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
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(updatedEvent, existingEvent);
    }

    @Override
    public EventDto convertToDTO(Event event) {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        return modelMapper.map(event, EventDto.class);
    }

    @Override
    public List<EventDto> getRecommendedEvents(Long studentId, Pageable pageable) {
        List<FavouriteEvent> favouriteEvents = favouriteEventRepository.findByStudentId(studentId);

        Map<Tag, Integer> tagCount = new HashMap<>();
        for (FavouriteEvent favouriteEvent : favouriteEvents) {
            for (Tag tag : favouriteEvent.getEvent().getTags()) {
                tagCount.put(tag, tagCount.getOrDefault(tag, 0) + 1);
            }
        }

        List<Map.Entry<Tag, Integer>> sortedTags = new ArrayList<>(tagCount.entrySet());
        sortedTags.sort(Map.Entry.<Tag, Integer>comparingByValue().reversed());

        List<Event> recommendedEvents = new ArrayList<>();
        for (Map.Entry<Tag, Integer> entry : sortedTags) {
            Tag tag = entry.getKey();
            Page<Event> eventsByTag = eventRepository.findEventByTags(tag, pageable);
            recommendedEvents.addAll(eventsByTag.getContent());
        }

        List<Event> favouriteEventsOnly = favouriteEvents.stream()
                .map(FavouriteEvent::getEvent)
                .toList();

        recommendedEvents.removeAll(favouriteEventsOnly);

        return recommendedEvents.stream().map((element) -> modelMapper.map(element, EventDto.class)).toList();
    }

}