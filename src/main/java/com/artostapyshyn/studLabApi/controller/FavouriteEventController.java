package com.artostapyshyn.studLabApi.controller;

import com.artostapyshyn.studLabApi.entity.Event;
import com.artostapyshyn.studLabApi.entity.FavouriteEvent;
import com.artostapyshyn.studLabApi.entity.Student;
import com.artostapyshyn.studLabApi.service.impl.EventServiceImpl;
import com.artostapyshyn.studLabApi.service.impl.FavouriteEventServiceImpl;
import com.artostapyshyn.studLabApi.service.impl.StudentServiceImpl;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/v1/favourites")
@AllArgsConstructor
public class FavouriteEventController {

    private final FavouriteEventServiceImpl favouriteEventService;

    private final StudentServiceImpl studentService;

    private final EventServiceImpl eventService;

    @PostMapping
    public FavouriteEvent addFavouriteEvent(@RequestParam("eventId") Long eventId, Authentication authentication) {
        Long studentId = getAuthStudentId(authentication);
        Event event = eventService.findEventById(eventId);
        FavouriteEvent favouriteEvent = new FavouriteEvent();
        favouriteEvent.setEvent(event);
        favouriteEvent.setStudentId(studentId);
        return favouriteEventService.save(favouriteEvent);
    }

    @DeleteMapping
    public void removeFavouriteEvent(Authentication authentication, @RequestParam("eventId") Long eventId) {
        Long studentId = getAuthStudentId(authentication);
        FavouriteEvent favouriteEvent = favouriteEventService.findByStudentIdAndEventId(studentId, eventId);
        favouriteEventService.delete(favouriteEvent);
    }

    private Long getAuthStudentId(Authentication authentication) {
        String studentEmail = authentication.getName();
        Student student = studentService.findByEmail(studentEmail);
        return student.getId();
    }

    @GetMapping
    public List<Event> getFavouriteEventsByUserId(Authentication authentication) {
        Long studentId = getAuthStudentId(authentication);
        List<FavouriteEvent> favouriteEvents = favouriteEventService.findByStudentId(studentId);
        return favouriteEvents.stream()
                .map(FavouriteEvent::getEvent)
                .toList();
    }
}