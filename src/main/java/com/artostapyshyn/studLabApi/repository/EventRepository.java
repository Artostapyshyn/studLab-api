package com.artostapyshyn.studLabApi.repository;

import com.artostapyshyn.studLabApi.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    Event findEventById(Long id);

    @Query("SELECT e FROM Event e ORDER BY e.favoriteCount DESC")
    List<Event> findPopularEvents();

    @Query("SELECT e FROM Event e ORDER BY e.date DESC")
    List<Event> findAllEventsByDateDesc();

    @Query("SELECT e FROM Event e ORDER BY e.creationDate DESC")
    List<Event> findAllEventsByCreationDateDesc();
}