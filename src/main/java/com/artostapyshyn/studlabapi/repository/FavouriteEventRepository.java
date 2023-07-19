package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.FavouriteEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavouriteEventRepository extends JpaRepository<FavouriteEvent, Long> {
    List<FavouriteEvent> findByStudentId(Long studentId);

    FavouriteEvent findByStudentIdAndEventId(Long studentId, Long eventId);

    boolean existsByEventIdAndStudentId(Long eventId, Long studentId);
}