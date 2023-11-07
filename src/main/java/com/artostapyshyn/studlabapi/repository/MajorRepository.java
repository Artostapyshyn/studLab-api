package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MajorRepository extends JpaRepository<Major, Long> {

    @Query("SELECT m FROM Major m ORDER BY m.name ASC")
    List<Major> findAll();

    Major findByName(String name);
}