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
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findEventById(Long id);

    @Query(value = "SELECT e FROM Event e WHERE e.endDate > CURRENT_TIMESTAMP " +
            "ORDER BY CASE WHEN e.eventType = 'PARTNER_EVENT' THEN 0 ELSE 1 END, e.endDate ASC, e.id ASC",
            countQuery = "SELECT COUNT(e) FROM Event e WHERE e.endDate > CURRENT_TIMESTAMP")
    Page<Event> findUpcomingEvents(Pageable pageable);


    @Query(value = "SELECT e FROM Event e JOIN FETCH e.tags t WHERE e.endDate > CURRENT_TIMESTAMP " +
            "ORDER BY e.favoriteCount DESC"
            , countQuery = "SELECT COUNT(e) FROM Event e WHERE e.endDate > CURRENT_TIMESTAMP")
    List<Event> findPopularEvents(Pageable pageable);

    @Query(value = "SELECT e FROM Event e WHERE e.endDate > CURRENT_TIMESTAMP " +
            "ORDER BY e.date ASC"
            , countQuery = "SELECT COUNT(e) FROM Event e WHERE e.endDate > CURRENT_TIMESTAMP")
    Page<Event> findAllEventsByDateAsc(Pageable pageable);

    @Query(value = "SELECT e FROM Event e WHERE e.endDate > CURRENT_TIMESTAMP " +
            "ORDER BY e.creationDate ASC"
            , countQuery = "SELECT COUNT(e) FROM Event e WHERE e.endDate > CURRENT_TIMESTAMP")
    Page<Event> findAllEventsByCreationDateAsc(Pageable pageable);

    @Query("SELECT DISTINCT e FROM Event e JOIN e.tags t WHERE t = :tag")
    Page<Event> findEventByTags(@Param("tag") Tag tag,
                                Pageable pageable);

    @Query(value = "SELECT e FROM Event e WHERE e.endDate > CURRENT_TIMESTAMP " +
            "ORDER BY CASE WHEN e.eventType = 'PARTNER_EVENT' THEN 0 ELSE 1 END, e.endDate ASC, e.id ASC")
    Event findByNameOfEvent(String eventName);

    List<Event> findAllByTagsIn(Set<Tag> tags, Pageable pageable);
}