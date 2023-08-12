package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.entity.FavouriteEvent;
import com.artostapyshyn.studlabapi.exception.exceptions.ResourceNotFoundException;
import com.artostapyshyn.studlabapi.repository.EventRepository;
import com.artostapyshyn.studlabapi.repository.FavouriteEventRepository;
import com.artostapyshyn.studlabapi.service.FavouriteEventService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FavouriteEventServiceImpl implements FavouriteEventService {

    private final FavouriteEventRepository favouriteEventRepository;

    private final EventRepository eventRepository;

    @Override
    public List<FavouriteEvent> findByStudentId(Long id) {
        return favouriteEventRepository.findByStudentId(id);
    }

    @Override
    public Optional<FavouriteEvent> findByStudentIdAndEventId(Long studentId, Long eventId) {
        return Optional.ofNullable(favouriteEventRepository.findByStudentIdAndEventId(studentId, eventId));
    }

    @Override
    @Transactional
    public FavouriteEvent save(FavouriteEvent favouriteEvent) {
        return favouriteEventRepository.save(favouriteEvent);
    }

    @Override
    public List<FavouriteEvent> findByEventId(Long eventId) {
        return favouriteEventRepository.findByEventId(eventId);
    }

    @Override
    @Transactional
    public void delete(FavouriteEvent favouriteEvent) {
        favouriteEventRepository.delete(favouriteEvent);
    }

    @Override
    @Transactional
    public void addToFavorites(Long eventId) {
        Event event = eventRepository.findEventById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id - " + eventId));
        event.setFavoriteCount(event.getFavoriteCount() + 1);
        eventRepository.save(event);
    }

    @Override
    @Transactional
    public void removeFromFavorites(Long eventId) {
        Event event = eventRepository.findEventById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id - " + eventId));
        event.setFavoriteCount(event.getFavoriteCount() - 1);
        eventRepository.save(event);
    }

    @Override
    public boolean isEventInFavorites(Long eventId, Long studentId) {
        return favouriteEventRepository.existsByEventIdAndStudentId(eventId, studentId);
    }

}
