package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Course;
import com.artostapyshyn.studlabapi.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    void findAllCoursesByCreationDateDesc() {
        List<Course> courses = Arrays.asList(
                createRandomCourse(),
                createRandomCourse(),
                createRandomCourse()
        );

        when(courseRepository.findAllCoursesByCreationDateDesc(Pageable.unpaged())).thenReturn((Page<Course>) courses);

        List<Course> foundCourses = courseService.findAllCoursesByCreationDateDesc(Pageable.unpaged());

        assertNotNull(foundCourses);
        assertEquals(courses.size(), foundCourses.size());

        for (Course foundCourse : foundCourses) {
            assertTrue(courses.contains(foundCourse));
        }
    }

    @Test
    void updateCourse() {
        Course course = createRandomCourse();

        when(courseRepository.existsById(course.getId())).thenReturn(true);
        when(courseRepository.save(course)).thenReturn(course);

        Course updatedCourse = course;
        updatedCourse.setCourseName("Updated name");

        courseService.updateCourse(course, updatedCourse);
        assertNotNull(updatedCourse);
        assertEquals(course.getId(), updatedCourse.getId());
        assertEquals("Updated name", updatedCourse.getCourseName());
    }
}