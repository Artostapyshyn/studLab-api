package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Course;
import com.artostapyshyn.studlabapi.exception.exceptions.ResourceNotFoundException;
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
    public ResponseEntity<Map<String, Object>> getAllCourses() {
        List<Course> courses = courseService.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put(CODE, "200");
        response.put(STATUS, SUCCESS);
        response.put(MESSAGE, "Courses retrieved successfully");
        response.put("courses", courses);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Sort courses by creation date")
    @GetMapping("/newest")
    public ResponseEntity<Map<String, Object>> getCoursesByNewestDate() {
        List<Course> courses = courseService.findAllCoursesByCreationDateDesc();
        log.info("Listing newest courses");

        Map<String, Object> response = new HashMap<>();
        response.put(CODE, "200");
        response.put(STATUS, SUCCESS);
        response.put(MESSAGE, "Newest courses retrieved successfully");
        response.put("courses", courses);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Add a course.")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addCourse(@RequestBody Course course) {
        Map<String, Object> response = new HashMap<>();

        byte[] imageBytes = course.getCoursePhoto();
        course.setCoursePhoto(imageBytes);

        try {
            Course savedCourse = courseService.save(course);
            log.info("New event added with id - " + savedCourse.getId());

            response.put(CODE, "200");
            response.put(STATUS, SUCCESS);
            response.put(MESSAGE, "Course added successfully");
            response.put("course", savedCourse);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while adding course");
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Edit a course.")
    @PutMapping("/edit")
    public ResponseEntity<Map<String, Object>> editCourse(@RequestBody Course course) {
        Optional<Course> editedCourse = courseService.findById(course.getId());
        Map<String, Object> response = new HashMap<>();
        if (editedCourse.isPresent()) {
            Course existingCourse = editedCourse.get();
            courseService.updateCourse(existingCourse, course);
            courseService.save(existingCourse);

            response.put(CODE, "200");
            response.put(STATUS, SUCCESS);
            response.put(MESSAGE, "Course edited successfully");
            response.put("course", existingCourse);

            return ResponseEntity.ok(response);
        } else {
            throw new ResourceNotFoundException("Course not found");
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

            response.put(CODE, "200");
            response.put(STATUS, SUCCESS);
            response.put(MESSAGE, "Course deleted successfully");

            return ResponseEntity.ok(response);
        } else {
            throw new ResourceNotFoundException("Course not found");
        }
    }
}
