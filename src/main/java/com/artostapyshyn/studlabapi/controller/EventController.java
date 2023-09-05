package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;

@Log4j2
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/events")
@CrossOrigin(maxAge = 3600, origins = "*")
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Get all events",
            security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/all")
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.findAll();
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Get events by id",
            security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/find-by-id")
    public ResponseEntity<Event> getEventById(@RequestParam("eventId") Long eventId) {
        Optional<Event> event = eventService.findEventById(eventId);
        return event.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Sort events by popularity",
            security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/popular")
    public ResponseEntity<List<Event>> getEventsByPopularity() {
        List<Event> events = eventService.findPopularEvents();
        log.info("Listing events by popularity");
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Sort events by creation date",
            security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/newest")
    public ResponseEntity<List<Event>> getEventsByNewestDate() {
        List<Event> events = eventService.findAllEventsByCreationDateDesc();
        log.info("Listing newest events");
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Get upcoming events",
            security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEvents() {
        List<Event> events = eventService.findAllEventsByDateDesc();
        log.info("Listing upcoming events");
        return ResponseEntity.ok(events);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Add an event.",
            security = @SecurityRequirement(name = "basicAuth"))
    @PostMapping("/add")
    public ResponseEntity<Event> addEvent(@RequestBody @NotNull Event event, Authentication authentication) {
        event.setEventType(event.getEventType());
        byte[] imageBytes = event.getEventPhoto();
        event.setEventPhoto(imageBytes);

        if(!event.getDate().isBefore(event.getEndDate()) || imageBytes == null){
            return ResponseEntity.badRequest().build();
        }

        try {
            Event savedEvent = eventService.save(event);
            log.info("New event added with id - " + savedEvent.getId());
            return ResponseEntity.ok(savedEvent);
        } catch (Exception e) {
            log.warn(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Edit an event.",
            security = @SecurityRequirement(name = "basicAuth"))
    @PutMapping("/edit")
    public ResponseEntity<Event> editEvent(@RequestBody @NotNull Event event, Authentication authentication) {
        Optional<Event> editedEvent = eventService.findEventById(event.getId());

        if (editedEvent.isPresent()) {
            Event existingEvent = editedEvent.get();
            eventService.updateEvent(existingEvent, event);
            eventService.save(existingEvent);
            return ResponseEntity.ok(existingEvent);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Delete an event by id.",
            security = @SecurityRequirement(name = "basicAuth"))
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteEvent(@RequestParam("eventId") Long eventId, Authentication authentication) {
        Optional<Event> existingEvent = eventService.findEventById(eventId);
        if (existingEvent.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            eventService.deleteById(eventId);

            response.put(MESSAGE, "Event deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
