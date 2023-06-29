package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.entity.FavouriteEvent;
import com.artostapyshyn.studlabapi.repository.EventRepository;
import com.artostapyshyn.studlabapi.repository.FavouriteEventRepository;
import com.artostapyshyn.studlabapi.service.FavouriteEventService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FavouriteEventServiceImpl implements FavouriteEventService {

    private final FavouriteEventRepository favouriteEventRepository;
    private final EventRepository eventRepository;

    @Override
    @Cacheable(value = "favouriteEventsByStudentId", key = "#id")
    public List<FavouriteEvent> findByStudentId(Long id) {
        return favouriteEventRepository.findByStudentId(id);
    }

    @Override
    @Cacheable(value = "favouriteEventByStudentIdAndEventId", key = "{#studentId, #eventId}")
    public Optional<FavouriteEvent> findByStudentIdAndEventId(Long studentId, Long eventId) {
        return Optional.ofNullable(favouriteEventRepository.findByStudentIdAndEventId(studentId, eventId));
    }

    @Override
    public FavouriteEvent save(FavouriteEvent favouriteEvent) {
        return favouriteEventRepository.save(favouriteEvent);
    }

    @Override
    public void delete(FavouriteEvent favouriteEvent) {
        favouriteEventRepository.delete(favouriteEvent);
    }

    @Override
    public void removeFromFavorites(Long eventId) {
        Optional<Event> event = eventRepository.findEventById(eventId);
        event.get().setFavoriteCount(event.get().getFavoriteCount() - 1);
        eventRepository.save(event.get());
    }

    @Override
    public void addToFavorites(Long eventId) {
        Optional<Event> event = eventRepository.findEventById(eventId);
        event.get().setFavoriteCount(event.get().getFavoriteCount() + 1);
        eventRepository.save(event.get());
    }
}
