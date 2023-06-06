package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
}