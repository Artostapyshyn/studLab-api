package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/events")
@CrossOrigin(origins = "https://stud-lab.vercel.app")
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Get all events")
    @GetMapping("/all")
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.findAll();
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
    public ResponseEntity<List<Event>> getUpcomingEvents() {
        List<Event> events = eventService.findAllEventsByDateDesc();
        log.info("Listing upcoming events");
        return ResponseEntity.ok(events);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Add an event.")
    @PostMapping("/add")
    public ResponseEntity<Event> addEvent(@RequestBody Event event) {
        event.setEventType(event.getEventType());
        byte[] imageBytes = event.getEventPhoto();
        event.setEventPhoto(imageBytes);

        try {
            Event savedEvent = eventService.save(event);
            log.info("New event added with id - " + savedEvent.getId());
            return ResponseEntity.ok(savedEvent);
        } catch (Exception e) {
            log.info("Error adding event");
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Edit an event.")
    @PutMapping("/edit")
    public ResponseEntity<Event> editEvent(@RequestBody Event event) {
        Optional<Event> editedEvent = eventService.findEventById(event.getId());

        if (editedEvent.isPresent()) {
            Event existingEvent = editedEvent.get();
            updateEvent(existingEvent, event);
            eventService.save(existingEvent);
            return ResponseEntity.ok(existingEvent);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private void updateEvent(Event existingEvent, Event updatedEvent) {
        Optional.ofNullable(updatedEvent.getVenue()).ifPresent(existingEvent::setVenue);
        Optional.ofNullable(updatedEvent.getDate()).ifPresent(existingEvent::setDate);
        Optional.ofNullable(updatedEvent.getDescription()).ifPresent(existingEvent::setDescription);
        Optional.ofNullable(updatedEvent.getNameOfEvent()).ifPresent(existingEvent::setNameOfEvent);
        Optional.ofNullable(updatedEvent.getEventPhoto()).ifPresent(existingEvent::setEventPhoto);
        Optional.ofNullable(updatedEvent.getEventType()).ifPresent(existingEvent::setEventType);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Delete an event by id.")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteEvent(@RequestParam("eventId") Long eventId) {

        Optional<Event> existingEvent = eventService.findEventById(eventId);
        if (existingEvent.isPresent()) {
            eventService.deleteById(eventId);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
