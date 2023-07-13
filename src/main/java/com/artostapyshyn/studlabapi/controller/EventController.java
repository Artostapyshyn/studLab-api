package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.exception.exceptions.ResourceNotFoundException;
import com.artostapyshyn.studlabapi.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;

@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/events")
@CrossOrigin(origins = "*")
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Get all events")
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllEvents() {
        List<Event> events = eventService.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put(CODE, "200");
        response.put(STATUS, SUCCESS);
        response.put(MESSAGE, "Events retrieved successfully");
        response.put("events", events);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Sort events by popularity")
    @GetMapping("/popular")
    public ResponseEntity<Map<String, Object>> getEventsByPopularity() {
        List<Event> events = eventService.findPopularEvents();
        log.info("Listing events by popularity");

        Map<String, Object> response = new HashMap<>();
        response.put(CODE, "200");
        response.put(STATUS, SUCCESS);
        response.put(MESSAGE, "Events sorted by popularity retrieved successfully");
        response.put("events", events);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Sort events by creation date")
    @GetMapping("/newest")
    public ResponseEntity<Map<String, Object>> getEventsByNewestDate() {
        List<Event> events = eventService.findAllEventsByCreationDateDesc();
        log.info("Listing newest events");

        Map<String, Object> response = new HashMap<>();
        response.put(CODE, "200");
        response.put(STATUS, SUCCESS);
        response.put(MESSAGE, "Events sorted by creation date retrieved successfully");
        response.put("events", events);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get upcoming events")
    @GetMapping("/upcoming")
    public ResponseEntity<Map<String, Object>> getUpcomingEvents() {
        List<Event> events = eventService.findAllEventsByDateDesc();
        log.info("Listing upcoming events");

        Map<String, Object> response = new HashMap<>();
        response.put(CODE, "200");
        response.put(STATUS, SUCCESS);
        response.put(MESSAGE, "Upcoming events retrieved successfully");
        response.put("events", events);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Add an event.")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addEvent(@RequestBody Event event) {
        Map<String, Object> response = new HashMap<>();

        event.setEventType(event.getEventType());
        byte[] imageBytes = event.getEventPhoto();
        event.setEventPhoto(imageBytes);

        try {
            Event savedEvent = eventService.save(event);
            log.info("New event added with id - " + savedEvent.getId());

            response.put(CODE, "200");
            response.put(STATUS, SUCCESS);
            response.put(MESSAGE, "Event added successfully");
            response.put("event", savedEvent);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while adding event");
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Edit an event.")
    @PutMapping("/edit")
    public ResponseEntity<Map<String, Object>> editEvent(@RequestBody Event event) {
        Optional<Event> editedEvent = eventService.findEventById(event.getId());

        if (editedEvent.isPresent()) {
            Event existingEvent = editedEvent.get();
            eventService.updateEvent(existingEvent, event);
            eventService.save(existingEvent);

            Map<String, Object> response = new HashMap<>();
            response.put(CODE, "200");
            response.put(STATUS, SUCCESS);
            response.put(MESSAGE, "Event edited successfully");
            response.put("event", existingEvent);

            return ResponseEntity.ok(response);
        } else {
            throw new ResourceNotFoundException("Event not found");
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Delete an event by id.")
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteEvent(@RequestParam("eventId") Long eventId) {
        Optional<Event> existingEvent = eventService.findEventById(eventId);
        if (existingEvent.isPresent()) {
            eventService.deleteById(eventId);

            Map<String, Object> response = new HashMap<>();
            response.put(CODE, "200");
            response.put(STATUS, SUCCESS);
            response.put(MESSAGE, "Event deleted successfully");

            return ResponseEntity.ok(response);
        } else {
            throw new ResourceNotFoundException("Event not found");
        }
    }
}
