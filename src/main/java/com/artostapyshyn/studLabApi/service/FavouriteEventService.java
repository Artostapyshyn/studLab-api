package com.artostapyshyn.studLabApi.service;

import com.artostapyshyn.studLabApi.entity.FavouriteEvent;

import java.util.List;
import java.util.Optional;

public interface FavouriteEventService {

    List<FavouriteEvent> findByStudentId(Long userId);

    Optional<FavouriteEvent> findByStudentIdAndEventId(Long userId, Long eventId);

    FavouriteEvent save(FavouriteEvent favouriteEvent);

    void delete(FavouriteEvent favouriteEvent);

    void removeFromFavorites(Long eventId);

    void addToFavorites(Long eventId);
}
