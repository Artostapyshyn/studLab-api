package com.artostapyshyn.studLabApi.repository;

import com.artostapyshyn.studLabApi.entity.FavouriteEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavouriteEventRepository extends JpaRepository<FavouriteEvent, Long> {
    List<FavouriteEvent> findByStudentId(Long studentId);

    FavouriteEvent findByStudentIdAndEventId(Long studentId, Long eventId);
}