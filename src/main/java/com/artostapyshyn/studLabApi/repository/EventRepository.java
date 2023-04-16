package com.artostapyshyn.studLabApi.repository;

import com.artostapyshyn.studLabApi.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    Event findEventById(Long id);
}