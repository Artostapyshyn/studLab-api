package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.dto.EventDto;
import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.entity.Tag;
import com.artostapyshyn.studlabapi.service.EventService;
import com.artostapyshyn.studlabapi.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
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

    private final TagService tagService;

    private final ModelMapper modelMapper;

    @Operation(summary = "Get all events",
            security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/all")
    public ResponseEntity<List<EventDto>> getAllEvents() {
        List<EventDto> events = eventService.findAll();
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Get events by id",
            security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/find-by-id")
    public ResponseEntity<EventDto> getEventById(@RequestParam("eventId") Long eventId) {
        Optional<EventDto> event = eventService.findEventDtoById(eventId);
        return event.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Sort events by popularity",
            security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/popular")
    public ResponseEntity<List<EventDto>> getEventsByPopularity() {
        List<EventDto> events = eventService.findPopularEvents();
        log.info("Listing events by popularity");
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Sort events by creation date",
            security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/newest")
    public ResponseEntity<List<EventDto>> getEventsByNewestDate() {
        List<EventDto> events = eventService.findAllEventsByCreationDateDesc();
        log.info("Listing newest events");
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Get upcoming events",
            security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/upcoming")
    public ResponseEntity<List<EventDto>> getUpcomingEvents() {
        List<EventDto> events = eventService.findAllEventsByDateDesc();
        log.info("Listing upcoming events");
        return ResponseEntity.ok(events);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Add an event.", security = @SecurityRequirement(name = "basicAuth"))
    @PostMapping("/add")
    public ResponseEntity<Event> addEvent(@RequestBody @NotNull EventDto eventDto, Authentication authentication) {
        try {
            if (!eventDto.getDate().isBefore(eventDto.getEndDate()) || eventDto.getEventPhoto() == null) {
                return ResponseEntity.badRequest().build();
            }

            Event event = modelMapper.map(eventDto, Event.class);

            Set<Tag> resolvedTags = tagService.resolveAndAddTags(eventDto.getTags());
            event.setTags(resolvedTags);
            Event savedEvent = eventService.save(event);

            log.info("New event added with id - " + savedEvent.getId());
            return ResponseEntity.ok(savedEvent);
        } catch (Exception e) {
            log.warn(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Edit an event.", security = @SecurityRequirement(name = "basicAuth"))
    @PutMapping("/edit")
    public ResponseEntity<EventDto> editEvent(@RequestBody @NotNull Event updatedEvent, Authentication authentication) {
        Optional<Event> existingEventOpt = eventService.findEventById(updatedEvent.getId());

        if (existingEventOpt.isPresent()) {
            Event existingEvent = existingEventOpt.get();
            eventService.updateEvent(existingEvent, updatedEvent);
            eventService.save(existingEvent);
            return ResponseEntity.ok(eventService.convertToDTO(existingEvent));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Delete an event by id.", security = @SecurityRequirement(name = "basicAuth"))
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
