package com.artostapyshyn.studLabApi.repository;

import com.artostapyshyn.studLabApi.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    Event findEventById(Long id);

    @Query("SELECT e FROM Event e LEFT JOIN e.favoritedCount u GROUP BY e.id ORDER BY COUNT(u) DESC")
    List<Event> findPopularEvents();

    @Query("SELECT e FROM Event e ORDER BY e.date ASC")
    List<Event> findAllOrderByDateAsc();

    @Query("SELECT e FROM Event e ORDER BY e.creationDate ASC")
    List<Event> findAllOrderByCreationDateAsc();
}