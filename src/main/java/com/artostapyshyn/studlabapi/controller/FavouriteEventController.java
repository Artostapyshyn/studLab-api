package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.entity.FavouriteEvent;
import com.artostapyshyn.studlabapi.service.impl.EventServiceImpl;
import com.artostapyshyn.studlabapi.service.impl.FavouriteEventServiceImpl;
import com.artostapyshyn.studlabapi.service.impl.StudentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;

@Log4j2
@RestController
@RequestMapping("/api/v1/favourites")
@CrossOrigin(maxAge = 3600, origins = "*")
@AllArgsConstructor
public class FavouriteEventController {

    private final FavouriteEventServiceImpl favouriteEventService;

    private final StudentServiceImpl studentService;

    private final EventServiceImpl eventService;

    @Operation(summary = "Add event to favourite")
    @PostMapping("/add-to-favorites")
    public ResponseEntity<Object> addFavouriteEvent(@RequestParam("eventId") Long eventId, Authentication authentication) {
        Long studentId = studentService.getAuthStudentId(authentication);
        Optional<Event> event = eventService.findEventById(eventId);

        if (event.isPresent()) {
            if (favouriteEventService.isEventInFavorites(eventId, studentId)) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Event already added"));
            }

            FavouriteEvent favouriteEvent = createFavouriteEvent(event.get(), studentId);
            favouriteEventService.addToFavorites(eventId);
            favouriteEventService.save(favouriteEvent);

            return ResponseEntity.ok(favouriteEvent);
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Remove event from favourite")
    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeFavouriteEvent(Authentication authentication,
                                                                    @RequestParam("eventId") Long eventId) {
        Map<String, Object> response = new HashMap<>();
        Long studentId = studentService.getAuthStudentId(authentication);
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
    public ResponseEntity<List<Event>> getFavouriteEventsByStudentId(Authentication authentication) {
        Long studentId = studentService.getAuthStudentId(authentication);

        if(studentId == null){
            return ResponseEntity.badRequest().build();
        }

        List<FavouriteEvent> favouriteEvents = favouriteEventService.findByStudentId(studentId);
        List<Event> events = favouriteEvents.stream()
                .map(FavouriteEvent::getEvent)
                .toList();

        return ResponseEntity.ok(events);
    }
}