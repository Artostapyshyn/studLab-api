package com.artostapyshyn.studLabApi.service;

import com.artostapyshyn.studLabApi.entity.FavouriteEvent;

import java.util.List;

public interface FavouriteEventService {

    List<FavouriteEvent> findByUserId(Long userId);

    FavouriteEvent findByUserIdAndEventId(Long userId, Long eventId);

    FavouriteEvent save(FavouriteEvent favouriteEvent);

    void delete(FavouriteEvent favouriteEvent);
}
