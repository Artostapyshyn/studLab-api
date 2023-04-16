package com.artostapyshyn.studLabApi.service.impl;

import com.artostapyshyn.studLabApi.entity.FavouriteEvent;
import com.artostapyshyn.studLabApi.repository.FavouriteEventRepository;
import com.artostapyshyn.studLabApi.service.FavouriteEventService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FavouriteEventServiceImpl implements FavouriteEventService {

    private final FavouriteEventRepository favouriteEventRepository;

    @Override
    public List<FavouriteEvent> findByUserId(Long id) {
        return favouriteEventRepository.findByUserId(id);
    }

    @Override
    public FavouriteEvent findByUserIdAndEventId(Long studentId, Long eventId) {
        return favouriteEventRepository.findByUserIdAndEventId(studentId, eventId);
    }

    @Override
    public FavouriteEvent save(FavouriteEvent favouriteEvent) {
        return favouriteEventRepository.save(favouriteEvent);
    }

    @Override
    public void delete(FavouriteEvent favouriteEvent) {
        favouriteEventRepository.delete(favouriteEvent);
    }
}
