package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Course;
import com.artostapyshyn.studlabapi.repository.CourseRepository;
import com.artostapyshyn.studlabapi.service.CourseService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Override
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Override
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    @Transactional
    @Override
    public Course save(Course course) {
        return null;
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        courseRepository.deleteById(id);
    }

    @Override
    @Cacheable("coursesByCreationDate")
    public List<Course> findAllCoursesByCreationDateDesc() {
        return courseRepository.findAllCoursesByCreationDateDesc();
    }

    @Transactional
    @Override
    public void updateCourse(Course existingCourse, Course updatedCourse) {
        Optional.ofNullable(updatedCourse.getCourseLink()).ifPresent(existingCourse::setCourseLink);
        Optional.ofNullable(updatedCourse.getCourseDescription()).ifPresent(existingCourse::setCourseDescription);
        Optional.ofNullable(updatedCourse.getCourseName()).ifPresent(existingCourse::setCourseName);
        Optional.ofNullable(updatedCourse.getCoursePhoto()).ifPresent(existingCourse::setCoursePhoto);
    }
}
