package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.Complaint;

import java.util.List;
import java.util.Optional;

public interface ComplaintService {

    void processComplaints(Complaint complaint);

    Complaint save(Complaint complaint);

    Optional<Complaint> findById(Long id);

    List<Complaint> findAll();

    List<Complaint> findClosedComplaints();

    Complaint saveComplaint(Complaint complaint);

    void delete(Complaint complaint);
}
