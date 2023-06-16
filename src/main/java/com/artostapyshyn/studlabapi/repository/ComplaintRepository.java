package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Optional<Complaint> findById(Long id);

    List<Complaint> findAll();

    @Query("SELECT c FROM Complaint c WHERE c.status = 'Закрито'")
    List<Complaint> findClosedComplaints();
}