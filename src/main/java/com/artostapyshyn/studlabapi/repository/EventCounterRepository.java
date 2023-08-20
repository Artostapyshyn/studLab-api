package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.EventCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventCounterRepository extends JpaRepository<EventCounter, Long> {
}