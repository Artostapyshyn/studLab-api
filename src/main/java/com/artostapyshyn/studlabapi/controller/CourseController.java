package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Course;
import com.artostapyshyn.studlabapi.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/api/v1/course")
@CrossOrigin(maxAge = 3600, origins = "*")
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Get all courses")
    @GetMapping("/all")
    public ResponseEntity<List<Course>> getAllCourses(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "25") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<Course> courses = courseService.findAllCoursesByCreationDateDesc(pageable);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Sort courses by creation date")
    @GetMapping("/newest")
    public ResponseEntity<List<Course>> getCoursesByNewestDate(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "25") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Course> courses = courseService.findAllCoursesByCreationDateDesc(pageable);
        return ResponseEntity.ok(courses);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Add a course.")
    @PostMapping("/add")
    public ResponseEntity<Course> addCourse(@RequestBody @NonNull Course course, Authentication authentication) {
        if (course.getCoursePhoto() != null && course.getCoursePhoto().length > 0){
            byte[] imageBytes = course.getCoursePhoto();
            course.setCoursePhoto(imageBytes);
        } else {
            return ResponseEntity.badRequest().build();
        }

        try {
            Course savedCourse = courseService.save(course);
            return ResponseEntity.ok(savedCourse);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Edit a course.")
    @PutMapping("/edit")
    public ResponseEntity<Course> editCourse(@RequestBody @NonNull Course course, Authentication authentication) {
        Optional<Course> editedCourse = courseService.findById(course.getId());

        if (editedCourse.isPresent()) {
            Course existingCourse = editedCourse.get();
            courseService.updateCourse(existingCourse, course);
            courseService.save(existingCourse);

            return ResponseEntity.ok(existingCourse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Delete a course by id.")
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteCourse(@RequestParam("courseId") Long courseId, Authentication authentication) {
        Optional<Course> existingCourse = courseService.findById(courseId);
        Map<String, Object> response = new HashMap<>();

        if (existingCourse.isPresent()) {
            courseService.deleteById(courseId);
            response.put(MESSAGE, "Course deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
