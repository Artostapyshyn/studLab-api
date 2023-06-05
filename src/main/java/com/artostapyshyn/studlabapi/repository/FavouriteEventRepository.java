package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.FavouriteEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavouriteEventRepository extends JpaRepository<FavouriteEvent, Long> {
    List<FavouriteEvent> findByStudentId(Long studentId);

    FavouriteEvent findByStudentIdAndEventId(Long studentId, Long eventId);
}