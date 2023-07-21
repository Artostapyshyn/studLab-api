package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Slider;
import com.artostapyshyn.studlabapi.exception.exceptions.ResourceNotFoundException;
import com.artostapyshyn.studlabapi.service.SliderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;

@Log4j2
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/slider")
@CrossOrigin(maxAge = 3600, origins = "*")
public class SliderController {

    private final SliderService sliderService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Get slider image by id.")
    @GetMapping("/find-by-id")
    public ResponseEntity<Slider> getSliderById(@RequestParam("sliderImageId") Long sliderImageId) {
        Optional<Slider> slider = sliderService.findById(sliderImageId);
        return slider.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all slider images.")
    @GetMapping("/all")
    public ResponseEntity<List<Slider>> getAllSliderImages() {
        List<Slider> sliders = sliderService.findAll();
        return ResponseEntity.ok(sliders);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Add images to slider.")
    @PostMapping("/add")
    public ResponseEntity<Slider> addSliderImages(@RequestBody @NotNull Slider slider) {
        byte[] imageBytes = slider.getSliderPhoto();
        slider.setSliderPhoto(imageBytes);

        try {
            Slider savedSlider = sliderService.save(slider);
            return ResponseEntity.ok(savedSlider);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Delete image from slider by id.")
    @DeleteMapping("/delete-by-id")
    public ResponseEntity<Map<String, Object>> deleteSliderImage(@RequestParam("sliderId") Long sliderId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Slider> existingSliderImage = sliderService.findById(sliderId);
        if (existingSliderImage.isPresent()) {
            sliderService.deleteById(sliderId);
            response.put(MESSAGE, "Slider image deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            throw new ResourceNotFoundException("Slider image not found");
        }
    }
}
