package com.artostapyshyn.studLabApi.controller;

import com.artostapyshyn.studLabApi.service.impl.EventServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "https://stud-lab-api.onrender.com", maxAge = 3600)
@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventServiceImpl eventService;

    @GetMapping
    public ResponseEntity<List<Object>> getAllEvents() {
        List<Object> response = new ArrayList<>();
        response.add(eventService.findAll());

        log.info("Listing all events");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
