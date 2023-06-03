package com.artostapyshyn.studLabApi.controller;

import com.artostapyshyn.studLabApi.entity.Event;
import com.artostapyshyn.studLabApi.enums.EventType;
import com.artostapyshyn.studLabApi.enums.Role;
import com.artostapyshyn.studLabApi.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(maxAge = 3600)
@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

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
    public ResponseEntity<List<Event>> getUpcomingEvents() {
        List<Event> events = eventService.findAllEventsByDateDesc();
        log.info("Listing upcoming events");
        return ResponseEntity.ok(events);
    }

    private boolean hasRole(Authentication authentication, String roleName) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(roleName));
    }

    @Operation(summary = "Add an event.")
    @PostMapping("/add")
    public ResponseEntity<?> addEvent(@RequestBody Event event, Authentication authentication) {
        Map<String, String> response = new HashMap<>();

        boolean isUniversityRepresentative = hasRole(authentication, Role.ROLE_UNIVERSITY_REPRESENTATIVE.getAuthority());
        boolean isModerator = hasRole(authentication, Role.ROLE_MODERATOR.getAuthority());
        boolean isAdmin = hasRole(authentication, Role.ROLE_ADMIN.getAuthority());

        if (isAdmin) {
            event.setEventType(event.getEventType());
        } else if (isModerator) {
            if (event.getEventType() != EventType.GENERAL_EVENT && event.getEventType() != EventType.PARTNER_EVENT) {
                response.put("message", "You can't add university event");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(response);
            }
        } else if (isUniversityRepresentative) {
            event.setEventType(EventType.UNIVERSITY_EVENT);
        } else {
            response.put("message", "Forbidden");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(response);
        }

        byte[] imageBytes = event.getEventPhoto();
        event.setEventPhoto(imageBytes);
        Event savedEvent = eventService.save(event);
        log.info("New event added with id - " + savedEvent.getId());
        return ResponseEntity.ok(savedEvent);
    }

    @Operation(summary = "Edit an event.")
    @PutMapping("/edit")
    public ResponseEntity<?> editEvent(@RequestBody Event event, Authentication authentication) {
        Map<String, String> response = new HashMap<>();

        Optional<Event> editedEvent = eventService.findEventById(event.getId());
        boolean isUniversityRepresentative = hasRole(authentication, Role.ROLE_UNIVERSITY_REPRESENTATIVE.getAuthority());
        boolean isModerator = hasRole(authentication, Role.ROLE_MODERATOR.getAuthority());
        boolean isAdmin = hasRole(authentication, Role.ROLE_ADMIN.getAuthority());

        if (editedEvent.isPresent()) {
            Event existingEvent = editedEvent.get();

            if (isAdmin || (isUniversityRepresentative && existingEvent.getEventType() == EventType.UNIVERSITY_EVENT) ||
                    (isModerator && (existingEvent.getEventType() == EventType.GENERAL_EVENT || existingEvent.getEventType() == EventType.PARTNER_EVENT))) {

                updateEvent(existingEvent, event);
                eventService.save(existingEvent);
                return ResponseEntity.ok(existingEvent);

            } else {
                response.put("message", "Forbidden");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(response);
            }
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

    @Operation(summary = "Delete an event by id.")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteEvent(@RequestParam("eventId") Long eventId, Authentication authentication) {
        Map<String, String> response = new HashMap<>();

        Optional<Event> existingEvent = eventService.findEventById(eventId);
        if (existingEvent.isPresent()) {
            Event event = existingEvent.get();
            boolean isUniversityRepresentative = hasRole(authentication, Role.ROLE_UNIVERSITY_REPRESENTATIVE.getAuthority());
            boolean isModerator = hasRole(authentication, Role.ROLE_MODERATOR.getAuthority());
            boolean isAdmin = hasRole(authentication, Role.ROLE_ADMIN.getAuthority());

            if (isAdmin) {
                eventService.deleteById(eventId);
                return ResponseEntity.ok().build();
            } else if (isUniversityRepresentative) {
                if (event.getEventType() != EventType.UNIVERSITY_EVENT) {
                    response.put("message", "You're not a university representative");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(response);
                }
                eventService.deleteById(eventId);
                return ResponseEntity.ok().build();
            } else if (isModerator) {
                if (event.getEventType() != EventType.GENERAL_EVENT && event.getEventType() != EventType.PARTNER_EVENT) {
                    response.put("message", "You're not a moderator");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(response);
                }
                eventService.deleteById(eventId);
                return ResponseEntity.ok().build();
            } else {
                response.put("message", "Forbidden");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(response);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
