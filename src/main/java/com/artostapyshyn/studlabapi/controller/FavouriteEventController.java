package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.dto.EventDto;
import com.artostapyshyn.studlabapi.entity.*;
import com.artostapyshyn.studlabapi.service.InterestService;
import com.artostapyshyn.studlabapi.service.impl.EventServiceImpl;
import com.artostapyshyn.studlabapi.service.impl.FavouriteEventServiceImpl;
import com.artostapyshyn.studlabapi.service.impl.StudentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;

@Log4j2
@RestController
@RequestMapping("/api/v1/favourites")
@CrossOrigin(maxAge = 3600, origins = "*")
@AllArgsConstructor
public class FavouriteEventController {

    private final FavouriteEventServiceImpl favouriteEventService;

    private final StudentServiceImpl studentService;

    private final InterestService interestService;

    private final EventServiceImpl eventService;

    private final ModelMapper modelMapper;

    @Operation(summary = "Add event to favourite")
    @PostMapping("/add-to-favorites")
    public ResponseEntity<Object> addFavouriteEvent(@RequestParam("eventId") Long eventId, Authentication authentication) {
        Long studentId = studentService.getAuthStudentId(authentication);

        Student student = studentService.findById(studentService.getAuthStudentId(authentication)).orElseThrow();
        student.setLastActiveDateTime(LocalDateTime.now());

        Optional<Event> event = eventService.findEventById(eventId);

        if (event.isPresent()) {
            if (favouriteEventService.isEventInFavorites(eventId, studentId)) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Event already added"));
            }

            Set<String> tagsNames = event.get().getTags().stream().map(Tag::getName).collect(Collectors.toSet());
            Set<Interest> currentInterests = student.getInterests();

            for (String tagName : tagsNames) {
                Interest interest = interestService.findByName(tagName);
                if (interest != null && currentInterests.stream().noneMatch(i -> i.getName().equals(interest.getName()))) {
                    interest.getInterestedStudents().add(student);
                }
            }
            studentService.save(student);

            FavouriteEvent favouriteEvent = createFavouriteEvent(event.get(), studentId);
            favouriteEventService.addToFavorites(eventId);
            favouriteEventService.save(favouriteEvent);

            return ResponseEntity.ok(favouriteEvent);
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Remove event from favourite")
    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeFavouriteEvent(Authentication authentication, @RequestParam("eventId") Long eventId) {
        Map<String, Object> response = new HashMap<>();
        Long studentId = studentService.getAuthStudentId(authentication);
        Student student = studentService.findById(studentId).orElseThrow();
        student.setLastActiveDateTime(LocalDateTime.now());

        Optional<FavouriteEvent> favouriteEvent = favouriteEventService.findByStudentIdAndEventId(studentId, eventId);
        if (favouriteEvent.isPresent()) {
            favouriteEventService.delete(favouriteEvent.get());
            favouriteEventService.removeFromFavorites(eventId);
            response.put(MESSAGE, "Event removed from favourites successfully");
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.notFound().build();
    }

    private FavouriteEvent createFavouriteEvent(Event event, Long studentId) {
        FavouriteEvent favouriteEvent = new FavouriteEvent();
        favouriteEvent.setEvent(event);
        favouriteEvent.setStudentId(studentId);
        return favouriteEvent;
    }

    @Operation(summary = "Get student favourite events")
    @GetMapping("/get")
    public ResponseEntity<List<EventDto>> getFavouriteEventsByStudentId(Authentication authentication) {
        Long studentId = studentService.getAuthStudentId(authentication);

        return getListResponseEntity(studentId);
    }

    @Operation(summary = "Get student favourite events by id")
    @GetMapping("/get-by-id")
    public ResponseEntity<List<EventDto>> getEventsByStudentId(@RequestParam("studentId") Long studentId) {
        return getListResponseEntity(studentId);
    }

    private ResponseEntity<List<EventDto>> getListResponseEntity(Long studentId) {
        if (studentId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<FavouriteEvent> favouriteEvents = favouriteEventService.findByStudentId(studentId);
        List<EventDto> events = favouriteEvents.stream()
                .map(FavouriteEvent::getEvent)
                .map(event -> modelMapper.map(event, EventDto.class))
                .toList();

        return ResponseEntity.ok(events);
    }
}