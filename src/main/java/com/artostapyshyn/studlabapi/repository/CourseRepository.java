package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findAll();

    Optional<Course> findById(Long id);

    @Query("SELECT c FROM Course c ORDER BY c.creationDate DESC")
    List<Course> findAllCoursesByCreationDateDesc();
}