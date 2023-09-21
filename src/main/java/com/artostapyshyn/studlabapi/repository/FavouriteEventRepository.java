package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.FavouriteEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavouriteEventRepository extends JpaRepository<FavouriteEvent, Long> {
    @Query("SELECT fe FROM FavouriteEvent fe JOIN FETCH fe.event WHERE fe.studentId = :studentId")
    List<FavouriteEvent> findByStudentId(Long studentId);

    List<FavouriteEvent> findByEventId(Long eventId);

    FavouriteEvent findByStudentIdAndEventId(Long studentId, Long eventId);

    boolean existsByEventIdAndStudentId(Long eventId, Long studentId);
}