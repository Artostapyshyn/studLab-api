package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.entity.Course;
import com.artostapyshyn.studlabapi.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/course")
@CrossOrigin(origins = "https://stud-lab.vercel.app")
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Get all courses")
    @GetMapping("/all")
    public ResponseEntity<List<Course>> getAllEvents() {
        List<Course> courses = courseService.findAll();
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Sort courses by creation date")
    @GetMapping("/newest")
    public ResponseEntity<List<Course>> getCoursesByNewestDate() {
        List<Course> courses = courseService.findAllCoursesByCreationDateDesc();
        log.info("Listing newest courses");
        return ResponseEntity.ok().body(courses);
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
            log.info("Error adding event");
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
            updateCourse(existingCourse, course);
            courseService.save(existingCourse);
            return ResponseEntity.ok(existingCourse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private void updateCourse(Course existingCourse, Course updatedCourse) {
        Optional.ofNullable(updatedCourse.getCourseLink()).ifPresent(existingCourse::setCourseLink);
        Optional.ofNullable(updatedCourse.getCourseDescription()).ifPresent(existingCourse::setCourseDescription);
        Optional.ofNullable(updatedCourse.getCourseName()).ifPresent(existingCourse::setCourseName);
        Optional.ofNullable(updatedCourse.getCoursePhoto()).ifPresent(existingCourse::setCoursePhoto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @Operation(summary = "Delete a course by id.")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCourse(@RequestParam("courseId") Long courseId) {
        Optional<Course> existingCourse = courseService.findById(courseId);
        if (existingCourse.isPresent()) {
            courseService.deleteById(courseId);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
