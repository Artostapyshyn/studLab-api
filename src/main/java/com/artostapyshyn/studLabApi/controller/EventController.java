package com.artostapyshyn.studLabApi.controller;

import com.artostapyshyn.studLabApi.entity.Event;
import com.artostapyshyn.studLabApi.service.impl.EventServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@CrossOrigin(maxAge = 3600)
@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventServiceImpl eventService;

    @Operation(summary = "Get all events")
    @GetMapping("/all")
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.findAll();
        log.info("Listing all events");
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Sort events by popularity")
    @GetMapping("/popular")
    public ResponseEntity<List<Event>> getEventsByPopularity() {
        List<Event> events = eventService.findPopularEvents();
        log.info("Listing events by popularity");
        return ResponseEntity.ok().body(events);
    }

    @Operation(summary = "Sort events by creation date")
    @GetMapping("/newest")
    public ResponseEntity<List<Event>> getEventsByNewestDate() {
        List<Event> events = eventService.findAllEventsByCreationDateDesc();
        log.info("Listing newest events");
        return ResponseEntity.ok().body(events);
    }

    @Operation(summary = "Get upcoming events")
    @GetMapping("/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEvents()  {
        List<Event> events = eventService.findAllEventsByDateDesc();
        log.info("Listing upcoming events");
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Add an event.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<?> addEvent(@RequestBody Event event, @RequestParam("image") MultipartFile image) {
        Event savedEvent = eventService.save(event);
        log.info("New event added with id - " + savedEvent.getId());
        return ResponseEntity.ok(savedEvent);
    }

    @Operation(summary = "Edit an event.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/edit")
    public ResponseEntity<?> editEvent(@RequestParam("eventId") Long eventId, @RequestParam Event event) {
        Optional<Event> optionalEvent = eventService.findEventById(eventId);
        if (optionalEvent.isPresent()) {
            Event existingEvent = optionalEvent.get();
            if (event.getVenue() != null) {
                existingEvent.setVenue(event.getVenue());
            }
            if (event.getDate() != null) {
                existingEvent.setDate(event.getDate());
            }
            if (event.getDescription() != null) {
                existingEvent.setDescription(event.getDescription());
            }
            if (event.getNameOfEvent() != null) {
                existingEvent.setNameOfEvent(event.getNameOfEvent());
            }
            if (event.getEventPhoto() != null) {
                existingEvent.setEventPhoto(event.getEventPhoto());
            }
            Event updatedEvent = eventService.save(existingEvent);
            return ResponseEntity.ok(updatedEvent);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete an event by id.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteEvent(@RequestParam("eventId") Long eventId) {
        Optional<Event> event = eventService.findEventById(eventId);
        if (event.isPresent()) {
            eventService.deleteById(eventId);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
