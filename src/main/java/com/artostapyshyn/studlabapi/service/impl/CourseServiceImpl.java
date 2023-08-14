package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Course;
import com.artostapyshyn.studlabapi.repository.CourseRepository;
import com.artostapyshyn.studlabapi.service.CourseService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
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
    @Cacheable(value = "allCourses")
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Override
    @Cacheable(value = "courseById")
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    @Transactional
    @CachePut(value = {"allCourses", "courseById"})
    @Override
    public Course save(Course course) {
        return courseRepository.save(course);
    }

    @Transactional
    @CacheEvict(value = {"allCourses", "courseById"})
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
    @CacheEvict(value = {"allCourses", "courseById", "coursesByCreationDate"})
    @Override
    public void updateCourse(Course existingCourse, Course updatedCourse) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(updatedCourse, existingCourse);
    }
}
