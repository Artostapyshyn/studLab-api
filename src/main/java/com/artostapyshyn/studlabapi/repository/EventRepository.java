package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findEventById(Long id);

    @Query("SELECT e FROM Event e LEFT JOIN e.tags WHERE e.endDate > CURRENT_TIMESTAMP " +
            "ORDER BY CASE WHEN e.eventType = 'PARTNER_EVENT' THEN 1 WHEN e.eventType = 'UNIVERSITY_EVENT' THEN 2 ELSE 3 END")
    Page<Event> findUpcomingEvents(Pageable pageable);

    @Query("SELECT e FROM Event e JOIN FETCH e.tags t WHERE e.endDate > CURRENT_TIMESTAMP ORDER BY e.favoriteCount DESC")
    List<Event> findPopularEvents(Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.endDate > CURRENT_TIMESTAMP ORDER BY e.date ASC")
    Page<Event> findAllEventsByDateAsc(Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.endDate > CURRENT_TIMESTAMP ORDER BY e.creationDate ASC")
    Page<Event> findAllEventsByCreationDateAsc(Pageable pageable);

    @Query("SELECT DISTINCT e FROM Event e JOIN e.tags t WHERE t = :tag")
    Page<Event> findEventByTags(@Param("tag") Tag tag,
                                Pageable pageable);
}