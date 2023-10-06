package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.entity.SubTag;
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

    @Query("SELECT e FROM Event e JOIN FETCH e.tags t ORDER BY e.favoriteCount DESC")
    List<Event> findPopularEvents();

    @Query("SELECT e FROM Event e ORDER BY e.date ASC")
    List<Event> findAllEventsByDateAsc();

    @Query("SELECT e FROM Event e ORDER BY e.creationDate ASC")
    List<Event> findAllEventsByCreationDateAsc();

    @Query("SELECT DISTINCT e FROM Event e JOIN e.tags t JOIN t.subTags st WHERE st = :subTag")
    Set<Event> findEventBySubTag(@Param("subTag") SubTag subTag);
}