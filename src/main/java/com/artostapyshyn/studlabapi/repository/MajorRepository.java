package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MajorRepository extends JpaRepository<Major, Long> {

    Major findByName(String name);
}