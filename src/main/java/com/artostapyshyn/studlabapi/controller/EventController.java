package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.dto.EventDto;
import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.entity.Tag;
import com.artostapyshyn.studlabapi.service.EventService;
import com.artostapyshyn.studlabapi.service.StudentService;
import com.artostapyshyn.studlabapi.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    private final StudentService studentService;

    private final ModelMapper modelMapper;

    @Operation(summary = "Get all events")
    @GetMapping("/all")
    public ResponseEntity<List<EventDto>> getAllEvents(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "25") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<EventDto> events = eventService.findUpcomingEvents(pageable);
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Get all event names")
    @GetMapping("/all-names")
    public ResponseEntity<List<Map<String, Object>>> getAllEventNames() {
        List<Map<String, Object>> eventData = eventService.findAll().stream()
                .map(student -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("nameOfEvent", student.getNameOfEvent());
                    return data;
                })
                .toList();

        return ResponseEntity.ok(eventData);
    }

    @Operation(summary = "Get recommended events with pagination")
    @GetMapping("/recommended")
    public ResponseEntity<List<EventDto>> getRecommendedEvents(Authentication authentication,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "25") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Long studentId = studentService.getAuthStudentId(authentication);
        List<EventDto> eventDtos = eventService.getRecommendedEvents(studentId, pageable);

        return ResponseEntity.ok(eventDtos);
    }

    @Operation(summary = "Sort events by popularity with pagination")
    @GetMapping("/popular")
    public ResponseEntity<List<EventDto>> getEventsByPopularity(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<EventDto> events = eventService.findPopularEvents(pageable);

        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Sort events by creation date with pagination")
    @GetMapping("/newest")
    public ResponseEntity<List<EventDto>> getEventsByNewestDate(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<EventDto> events = eventService.findAllEventsByCreationDateAsc(pageable);

        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Get upcoming events with pagination")
    @GetMapping("/upcoming")
    public ResponseEntity<List<EventDto>> getUpcomingEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<EventDto> events = eventService.findAllEventsByDateAsc(pageable);

        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Get events by id")
    @GetMapping("/find-by-id")
    public ResponseEntity<EventDto> getEventById(@RequestParam("eventId") Long eventId) {
        Optional<EventDto> event = eventService.findEventDtoById(eventId);
        return event.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Add an event.")
    @PostMapping("/add")
    public ResponseEntity<Event> addEvent(@RequestBody @NotNull EventDto eventDto) {

        if (!isValidEventDto(eventDto)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Event event = modelMapper.map(eventDto, Event.class);

            Set<Tag> resolvedTags = tagService.resolveAndAddTags(eventDto.getTags());
            event.setTags(resolvedTags);

            Event savedEvent = eventService.save(event);
            return ResponseEntity.ok(savedEvent);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private boolean isValidEventDto(EventDto eventDto) {
        return !eventDto.getDate().isAfter(eventDto.getEndDate()) && eventDto.getEventPhoto() != null;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Edit an event.")
    @PutMapping("/edit")
    public ResponseEntity<EventDto> editEvent(@RequestBody @NotNull Event updatedEvent) {
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
    @Operation(summary = "Delete an event by id.")
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteEvent(@RequestParam("eventId") Long eventId) {
        Optional<Event> existingEvent = eventService.findEventById(eventId);

        if (existingEvent.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            try {
                existingEvent.get().getTags().clear();
                eventService.deleteById(eventId);
                response.put(MESSAGE, "Event deleted successfully");
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                response.put(MESSAGE, "Error while deleting event. Please check server logs for details.");
                return ResponseEntity.internalServerError().body(response);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
