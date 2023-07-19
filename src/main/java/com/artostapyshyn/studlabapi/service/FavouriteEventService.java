package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.FavouriteEvent;

import java.util.List;
import java.util.Optional;

public interface FavouriteEventService {

    List<FavouriteEvent> findByStudentId(Long userId);

    Optional<FavouriteEvent> findByStudentIdAndEventId(Long userId, Long eventId);

    FavouriteEvent save(FavouriteEvent favouriteEvent);

    void delete(FavouriteEvent favouriteEvent);

    void removeFromFavorites(Long eventId);

    void addToFavorites(Long eventId);

    boolean isEventInFavorites(Long eventId, Long studentId);
}
