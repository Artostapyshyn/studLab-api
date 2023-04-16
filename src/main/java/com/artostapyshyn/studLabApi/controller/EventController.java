package com.artostapyshyn.studLabApi.controller;

import com.artostapyshyn.studLabApi.entity.Event;
import com.artostapyshyn.studLabApi.service.impl.EventServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventServiceImpl eventService;

    @GetMapping
    public ResponseEntity<List<Object>> getAllEvents(){
        List<Object> response = new ArrayList<>();
        response.add(eventService.findAll());

        log.info("Listing all events");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
