package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.dto.InterestDto;
import com.artostapyshyn.studlabapi.entity.Interest;
import com.artostapyshyn.studlabapi.service.InterestService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/v1/interests")
@CrossOrigin(maxAge = 3600, origins = "*")
@AllArgsConstructor
public class InterestController {

    private final InterestService interestService;

    private final ModelMapper modelMapper;

    @Operation(summary = "Get all interests.")
    @GetMapping("/all")
    public ResponseEntity<List<InterestDto>> getAllInterests() {
        List<InterestDto> interests = interestService.findAll();
        return ResponseEntity.ok(interests);
    }

    @Operation(summary = "Add interest.")
    @PostMapping("/add")
    public ResponseEntity<Interest> addEvent(@RequestBody @NotNull InterestDto interestDto) {
        try {
            Interest interest = modelMapper.map(interestDto, Interest.class);

            Interest savedInterest = interestService.save(interest);
            return ResponseEntity.ok(savedInterest);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
