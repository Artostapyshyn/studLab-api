package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.*;
import com.artostapyshyn.studlabapi.enums.ComplaintType;
import com.artostapyshyn.studlabapi.repository.ComplaintRepository;
import com.artostapyshyn.studlabapi.service.*;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;

    private final CommentService commentService;

    private final StudentService studentService;

    @Override
    @Scheduled(fixedDelay = 120000)
    public void processStudentProfileComplaints() {
        List<Complaint> complaints = complaintRepository.findAll();
        for (Complaint complaint : complaints) {
            Optional<Student> student = studentService.findById(complaint.getStudentId());
            if ((student.isPresent() && student.get().getPhotoBytes() == null)
                    || (student.get().getFirstName() == null || student.get().getLastName() == null)) {
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
            if (comment.isEmpty() || !comment.get().getStudent().isCanWriteComments()) {
                complaint.setStatus("Закрито");
                complaintRepository.save(complaint);
            }

            Long studentId = complaint.getStudentId();
            Student student = studentService.findById(studentId)
                    .orElseThrow(() -> new NotFoundException("Student not found."));
            student.setCanWriteComments(false);
            LocalDateTime blockedUntil = LocalDateTime.now().plusHours(24);
            student.setBlockedUntil(blockedUntil);
            studentService.save(student);
        }
    }

    @Override
    public Complaint saveComplaint(Complaint complaint) {
        Complaint savedComplaint = new Complaint();
        savedComplaint.setComplaintReason(complaint.getComplaintReason());
        savedComplaint.setStatus("Відкритий");

        if (complaint.getCommentId() != null) {
            Comment comment = commentService.findById(complaint.getCommentId())
                    .orElseThrow(() -> new NotFoundException("Comment not found with ID: " + complaint.getCommentId()));
            savedComplaint.setCommentId(comment.getId());
            savedComplaint.setType(ComplaintType.COMMENT_COMPLAINT);
        }

        if (complaint.getStudentId() != null) {
            Student student = studentService.findById(complaint.getStudentId())
                    .orElseThrow(() -> new NotFoundException("Student not found with ID: " + complaint.getStudentId()));
            savedComplaint.setStudentId(student.getId());
            savedComplaint.setType(ComplaintType.STUDENT_PROFILE_COMPLAINT);
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
