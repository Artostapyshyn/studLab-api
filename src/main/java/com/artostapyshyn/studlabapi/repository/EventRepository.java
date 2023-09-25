package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findEventById(Long id);

    @Query("SELECT e FROM Event e ORDER BY e.favoriteCount DESC")
    List<Event> findPopularEvents();

    @Query("SELECT e FROM Event e ORDER BY e.date ASC")
    List<Event> findAllEventsByDateAsc();

    @Query("SELECT e FROM Event e ORDER BY e.creationDate ASC")
    List<Event> findAllEventsByCreationDateAsc();

    @Query("SELECT e FROM Event e JOIN e.tags t WHERE t = :tag")
    List<Event> findEventByTags(@Param("tag") Tag tag);
}