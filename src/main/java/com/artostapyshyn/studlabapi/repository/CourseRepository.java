package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c ORDER BY c.creationDate DESC")
    List<Course> findAllCoursesByCreationDateDesc();
}