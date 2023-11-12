package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Course;
import com.artostapyshyn.studlabapi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomCourse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        courseService = new CourseServiceImpl(courseRepository, modelMapper);
    }

    @Test
    void findAll() {
        List<Course> courses = Arrays.asList(
                createRandomCourse(),
                createRandomCourse(),
                createRandomCourse()
        );

        when(courseRepository.findAll()).thenReturn(courses);

        List<Course> foundCourses = courseService.findAll();

        assertNotNull(foundCourses);
        assertEquals(courses.size(), foundCourses.size());

        for (Course foundCourse : foundCourses) {
            assertTrue(courses.contains(foundCourse));
        }
    }

    @Test
    void findById() {
        Course course = createRandomCourse();
        when(courseRepository.save(course)).thenReturn(course);

        courseService.findById(course.getId());
        assertNotNull(course);
    }

    @Test
    void deleteById() {
        Long courseId = 123L;

        courseService.deleteById(courseId);
        verify(courseRepository, times(1)).deleteById(courseId);
    }

    @Test
    void updateCourse() {
        Course course = createRandomCourse();
        when(courseRepository.save(course)).thenReturn(course);
        Course updatedCourse = createRandomCourse();
        when(courseRepository.save(updatedCourse)).thenReturn(updatedCourse);
        courseService.updateCourse(course, updatedCourse);
        assertNotNull(course);
    }
}