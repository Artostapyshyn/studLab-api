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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/api/v1/favourites")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class FavouriteEventController {

    private final FavouriteEventServiceImpl favouriteEventService;

    private final StudentServiceImpl studentService;

    private final EventServiceImpl eventService;

    @Operation(summary = "Add event to favourite")
    @PostMapping("/add-to-favorites")
    public Object addFavouriteEvent(@RequestParam("eventId") Long eventId, Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        Long studentId = studentService.getAuthStudentId(authentication);
        Optional<Event> event = eventService.findEventById(eventId);
        if (event.isPresent()) {
            FavouriteEvent favouriteEvent = new FavouriteEvent();
            favouriteEvent.setEvent(event.get());
            favouriteEvent.setStudentId(studentId);
            favouriteEventService.addToFavorites(eventId);
            return favouriteEventService.save(favouriteEvent);
        }
        response.put("error", "Event not found");
        return ResponseEntity.badRequest().body(response);
    }

    @Operation(summary = "Remove event from favourite")
    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, String>> removeFavouriteEvent(Authentication authentication, @RequestParam("eventId") Long eventId) {
        Map<String, String> response = new HashMap<>();
        Long studentId = studentService.getAuthStudentId(authentication);
        Optional<FavouriteEvent> favouriteEvent = favouriteEventService.findByStudentIdAndEventId(studentId, eventId);
        if (favouriteEvent.isPresent()) {
            favouriteEventService.removeFromFavorites(eventId);
            favouriteEventService.delete(favouriteEvent.get());
            response.put("status", "deleted");
            return ResponseEntity.ok().body(response);
        }
        response.put("error", "Event not found");
        return ResponseEntity.badRequest().body(response);
    }

    @Operation(summary = "Get student favourite events")
    @GetMapping("/getFavourite")
    public List<Event> getFavouriteEventsByStudentId(Authentication authentication) {
        Long studentId = studentService.getAuthStudentId(authentication);
        List<FavouriteEvent> favouriteEvents = favouriteEventService.findByStudentId(studentId);
        return favouriteEvents.stream()
                .map(FavouriteEvent::getEvent)
                .toList();
    }
}