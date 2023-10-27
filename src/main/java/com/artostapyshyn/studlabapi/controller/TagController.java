package com.artostapyshyn.studlabapi.controller;


import com.artostapyshyn.studlabapi.dto.TagDto;
import com.artostapyshyn.studlabapi.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/v1/tags")
@CrossOrigin(maxAge = 3600, origins = "*")
@AllArgsConstructor
public class TagController {
    private final TagService tagService;

    @Operation(summary = "Get all tags.")
    @GetMapping("/all")
    public ResponseEntity<List<TagDto>> getAllInterests() {
        List<TagDto> tags = tagService.findAll();
        return ResponseEntity.ok(tags);
    }
}
