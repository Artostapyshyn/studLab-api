package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c ORDER BY c.creationDate DESC")
    Page<Course> findAllCoursesByCreationDateDesc(Pageable pageable);
}