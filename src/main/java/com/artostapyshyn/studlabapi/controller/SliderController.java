package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Slider;
import com.artostapyshyn.studlabapi.exception.exceptions.ResourceNotFoundException;
import com.artostapyshyn.studlabapi.service.SliderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;

@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/slider")
@CrossOrigin(origins = "*")
public class SliderController {

    private final SliderService sliderService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Get slider image by id.")
    @GetMapping("/find-by-id")
    public ResponseEntity<Map<String, Object>> getSliderById(@RequestParam("sliderImageId") Long sliderImageId) {
        Optional<Slider> slider = sliderService.findById(sliderImageId);
        if (slider.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put(CODE, "200");
            response.put(STATUS, SUCCESS);
            response.put(MESSAGE, "Slider image found");
            response.put("slider", slider.get());

            return ResponseEntity.ok(response);
        } else {
            throw new ResourceNotFoundException("Slider image not found");
        }
    }

    @Operation(summary = "Get all slider images.")
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllSliderImages() {
        List<Slider> sliders = sliderService.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put(CODE, "200");
        response.put(STATUS, SUCCESS);
        response.put(MESSAGE, "Slider images retrieved successfully");
        response.put("sliders", sliders);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Add images to slider.")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addSliderImages(@RequestBody Slider slider) {
        byte[] imageBytes = slider.getSliderPhoto();
        slider.setSliderPhoto(imageBytes);

        try {
            Slider savedSlider = sliderService.save(slider);

            Map<String, Object> response = new HashMap<>();
            response.put(CODE, "200");
            response.put(STATUS, SUCCESS);
            response.put(MESSAGE, "Slider image added successfully");
            response.put("slider", savedSlider);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while adding slider image");
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Delete image from slider by id.")
    @DeleteMapping("/delete-by-id")
    public ResponseEntity<Map<String, Object>> deleteSliderImage(@RequestParam("sliderId") Long sliderId) {
        Optional<Slider> existingSliderImage = sliderService.findById(sliderId);
        if (existingSliderImage.isPresent()) {
            sliderService.deleteById(sliderId);

            Map<String, Object> response = new HashMap<>();
            response.put(CODE, "200");
            response.put(STATUS, SUCCESS);
            response.put(MESSAGE, "Slider image deleted successfully");

            return ResponseEntity.ok(response);
        } else {
            throw new ResourceNotFoundException("Slider image not found");
        }
    }
}
