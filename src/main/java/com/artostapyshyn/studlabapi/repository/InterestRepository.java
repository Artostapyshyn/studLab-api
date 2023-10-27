package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestRepository extends JpaRepository<Interest, Long> {
}