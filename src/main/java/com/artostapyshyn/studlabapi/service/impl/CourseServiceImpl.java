package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Course;
import com.artostapyshyn.studlabapi.repository.CourseRepository;
import com.artostapyshyn.studlabapi.service.CourseService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    private final ModelMapper modelMapper;

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
        return courseRepository.save(course);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        courseRepository.deleteById(id);
    }

    @Override
    public List<Course> findAllCoursesByCreationDateDesc(Pageable pageable) {
        return courseRepository.findAllCoursesByCreationDateDesc(pageable).stream().toList();
    }

    @Transactional
    @Override
    public void updateCourse(Course existingCourse, Course updatedCourse) {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(updatedCourse, existingCourse);
    }
}
