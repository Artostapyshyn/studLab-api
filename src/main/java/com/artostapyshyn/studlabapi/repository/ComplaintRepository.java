package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Optional<Complaint> findById(Long id);

    List<Complaint> findAll();

}