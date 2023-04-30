package com.artostapyshyn.studLabApi.controller;

import com.artostapyshyn.studLabApi.entity.Event;
import com.artostapyshyn.studLabApi.service.impl.EventServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "https://stud-lab-api.onrender.com", maxAge = 3600)
@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventServiceImpl eventService;

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.findAll();
        log.info("Listing all events");
        return ResponseEntity.ok(events);
    }

    @GetMapping("/popularity")
    public ResponseEntity<List<Event>> getEventsByPopularity() {
        List<Event> events = eventService.findPopularEvents();
        log.info("Listing events by popularity");
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/newest")
    public ResponseEntity<List<Event>> getEventsByNewestDate() {
        List<Event> events = eventService.findAllOrderByCreationDateAsc();
        log.info("Listing newest events");
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEvents()  {
        List<Event> events = eventService.findAllOrderByDateAsc();
        log.info("Listing upcoming events");
        return new ResponseEntity<>(events, HttpStatus.OK);
    }
}
