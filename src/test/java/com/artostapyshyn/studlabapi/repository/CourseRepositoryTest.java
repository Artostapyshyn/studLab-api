package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Course;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomCourse;

@ActiveProfiles("test")
@DataJpaTest
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAll() {
        Course course1 = createRandomCourse();
        Course course2 = createRandomCourse();
        entityManager.persist(course1);
        entityManager.persist(course2);
        entityManager.flush();

        List<Course> courses = courseRepository.findAll();

        Assertions.assertEquals(2, courses.size());
        Assertions.assertNotNull(courses);
    }

    @Test
    void findById() {
        Course course = createRandomCourse();
        entityManager.persist(course);
        entityManager.flush();

        Optional<Course> result = courseRepository.findById(course.getId());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(course.getId(), result.get().getId());
    }

    @Test
    void findAllCoursesByCreationDateDesc() {
        Course course1 = createRandomCourse();
        course1.setCreationDate(LocalDateTime.of(2023, 7, 1, 12, 0));

        Course course2 = createRandomCourse();
        course2.setCreationDate(LocalDateTime.of(2023, 7, 2, 12, 0));

        entityManager.persist(course1);
        entityManager.persist(course2);
        entityManager.flush();

        List<Course> coursesByCreationDateDesc = courseRepository.findAllCoursesByCreationDateDesc();

        Assertions.assertEquals(2, coursesByCreationDateDesc.size());
        Assertions.assertNotNull(coursesByCreationDateDesc);
        Assertions.assertEquals(course2.getCreationDate(), coursesByCreationDateDesc.get(0).getCreationDate());
        Assertions.assertEquals(course1.getCreationDate(), coursesByCreationDateDesc.get(1).getCreationDate());
    }

}