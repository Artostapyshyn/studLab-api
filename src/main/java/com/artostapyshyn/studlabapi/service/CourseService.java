package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.Course;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CourseService {

    List<Course> findAll();

    Optional<Course> findById(Long id);

    Course save(Course course);

    void deleteById(Long id);

    List<Course> findAllCoursesByCreationDateDesc(Pageable pageable);

    void updateCourse(Course existingCourse, Course updatedCourse);
}
