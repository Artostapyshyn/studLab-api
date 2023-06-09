package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.*;
import com.artostapyshyn.studlabapi.enums.ComplaintType;
import com.artostapyshyn.studlabapi.repository.ComplaintRepository;
import com.artostapyshyn.studlabapi.service.*;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;

    private final CommentService commentService;

    private final VacancyService vacancyService;

    private final EventService eventService;

    private final StudentService studentService;

    @Override
    @Scheduled(fixedDelay = 120000)
    public void processProfilePhotoComplaints() {
        List<Complaint> complaints = complaintRepository.findAll();
        for (Complaint complaint : complaints) {
            Optional<Student> student = studentService.findById(complaint.getStudentId());
            if (student.isPresent() && student.get().getPhotoBytes() == null) {
                complaint.setStatus("Виконано");
                complaintRepository.save(complaint);
            }
        }
    }

    @Override
    @Scheduled(fixedDelay = 120000)
    public void processCommentComplaints() {
        List<Complaint> complaints = complaintRepository.findAll();
        for (Complaint complaint : complaints) {
            Optional<Comment> comment = commentService.findById(complaint.getCommentId());
            if (comment.isEmpty()) {
                complaint.setStatus("Закрито");
                complaintRepository.save(complaint);
            }
        }
    }

    @Override
    @Scheduled(fixedDelay = 120000)
    public void processEventComplaints() {
        List<Complaint> complaints = complaintRepository.findAll();
        for (Complaint complaint : complaints) {
            Optional<Event> event = eventService.findEventById(complaint.getEventId());
            if (event.isEmpty()) {
                complaint.setStatus("Закрито");
                complaintRepository.save(complaint);
            }
        }
    }

    @Override
    @Scheduled(fixedDelay = 120000)
    public void processVacancyComplaints() {
        List<Complaint> complaints = complaintRepository.findAll();
        for (Complaint complaint : complaints) {
            Optional<Vacancy> vacancy = vacancyService.findVacancyById(complaint.getVacancyId());
            if (vacancy.isEmpty()) {
                complaint.setStatus("Закрито");
                complaintRepository.save(complaint);
            }
        }
    }

    @Override
    public Complaint saveComplaint(Complaint complaint) {
        Complaint savedComplaint = new Complaint();
        savedComplaint.setComplaintText(complaint.getComplaintText());
        savedComplaint.setStatus("Відкритий");
        if (complaint.getEventId() != null) {
            Event event = eventService.findEventById(complaint.getEventId())
                    .orElseThrow(() -> new NotFoundException("Event not found with ID: " + complaint.getEventId()));
            savedComplaint.setEventId(event.getId());
            savedComplaint.setType(ComplaintType.EVENT_COMPLAINT);
        }

        if (complaint.getCommentId() != null) {
            Comment comment = commentService.findById(complaint.getCommentId())
                    .orElseThrow(() -> new NotFoundException("Comment not found with ID: " + complaint.getCommentId()));
            savedComplaint.setCommentId(comment.getId());
            savedComplaint.setType(ComplaintType.COMMENT_COMPLAINT);
        }

        if (complaint.getVacancyId() != null) {
            Vacancy vacancy = vacancyService.findVacancyById(complaint.getVacancyId())
                    .orElseThrow(() -> new NotFoundException("Vacancy not found with ID: " + complaint.getVacancyId()));
            savedComplaint.setVacancyId(vacancy.getId());
            savedComplaint.setType(ComplaintType.VACANCY_COMPLAINT);
        }

        if (complaint.getStudentId() != null) {
            Student student = studentService.findById(complaint.getStudentId())
                    .orElseThrow(() -> new NotFoundException("Student not found with ID: " + complaint.getStudentId()));
            savedComplaint.setStudentId(student.getId());
            savedComplaint.setType(ComplaintType.PROFILE_PHOTO_COMPLAINT);
        }
        return complaintRepository.save(savedComplaint);
    }

    @Override
    public void delete(Complaint complaint) {
        complaintRepository.delete(complaint);
    }

    @Override
    public Complaint save(Complaint complaint) {
        return complaintRepository.save(complaint);
    }

    @Override
    public Optional<Complaint> findById(Long id) {
        return complaintRepository.findById(id);
    }

    @Override
    public List<Complaint> findAll() {
        return complaintRepository.findAll().stream().toList();
    }
}
