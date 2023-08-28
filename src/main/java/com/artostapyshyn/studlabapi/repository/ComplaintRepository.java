package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    @Query("SELECT c FROM Complaint c WHERE c.status = 'Закрито'")
    List<Complaint> findClosedComplaints();

    @Query("SELECT c FROM Complaint c WHERE c.status = 'Відкрито'")
    List<Complaint> findOpenedComplaints();
}