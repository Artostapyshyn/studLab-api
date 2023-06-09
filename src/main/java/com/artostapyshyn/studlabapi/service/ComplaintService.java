package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.Complaint;

import java.util.List;
import java.util.Optional;

public interface ComplaintService {

    void processProfilePhotoComplaints();

    void processCommentComplaints();

    void processEventComplaints();

    void processVacancyComplaints();

    Complaint save(Complaint complaint);

    Optional<Complaint> findById(Long id);

    List<Complaint> findAll();

    Complaint saveComplaint(Complaint complaint);

    void delete(Complaint complaint);
}
