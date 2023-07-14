package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Course;
import com.artostapyshyn.studlabapi.service.CourseService;
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
@RequestMapping("/api/v1/course")
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Get all courses")
    @GetMapping("/all")
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.findAll();
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Sort courses by creation date")
    @GetMapping("/newest")
    public ResponseEntity<List<Course>> getCoursesByNewestDate() {
        List<Course> courses = courseService.findAllCoursesByCreationDateDesc();
        log.info("Listing newest courses");
        return ResponseEntity.ok(courses);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Add a course.")
    @PostMapping("/add")
    public ResponseEntity<Course> addCourse(@RequestBody Course course) {
        byte[] imageBytes = course.getCoursePhoto();
        course.setCoursePhoto(imageBytes);

        try {
            Course savedCourse = courseService.save(course);
            log.info("New event added with id - " + savedCourse.getId());
            return ResponseEntity.ok(savedCourse);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Edit a course.")
    @PutMapping("/edit")
    public ResponseEntity<Course> editCourse(@RequestBody Course course) {
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
    public ResponseEntity<Map<String, Object>> deleteCourse(@RequestParam("courseId") Long courseId) {
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
