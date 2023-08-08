package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.dto.ComplaintDto;
import com.artostapyshyn.studlabapi.entity.*;
import com.artostapyshyn.studlabapi.exception.exceptions.ResourceNotFoundException;
import com.artostapyshyn.studlabapi.repository.ComplaintRepository;
import com.artostapyshyn.studlabapi.service.CommentService;
import com.artostapyshyn.studlabapi.service.ComplaintService;
import com.artostapyshyn.studlabapi.service.StudentService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;

    private final CommentService commentService;

    private final StudentService studentService;

    private final ModelMapper modelMapper;

    @Override
    public void processComplaints(Complaint complaint) {
        studentService.findById(complaint.getStudentId())
                .ifPresent(student -> commentService.findById(complaint.getCommentId())
                        .ifPresent(comment -> handleComplaint(student, comment, complaint)));
    }

    private void handleComplaint(Student student, Comment comment, Complaint complaint) {
        if (complaint.isDeleteComment()) {
            commentService.delete(comment);
        }

        if (complaint.isBlockUser()) {
            switch (complaint.getBlockDuration()) {
                case "week" -> student.setBlockedUntil(LocalDateTime.now().plusWeeks(1));
                case "month" -> student.setBlockedUntil(LocalDateTime.now().plusMonths(1));
                case "forever" -> student.setBlockedUntil(LocalDateTime.now().plusYears(10));
            }
        }
        if (complaint.isCloseComplaint()) {
            complaint.setStatus("Закрито");
        }
        complaintRepository.save(complaint);
    }

    @Override
    public Complaint saveComplaint(ComplaintDto complaintDto) {
        Complaint savedComplaint = new Complaint();
        savedComplaint.setComplaintReason(complaintDto.getComplaintReason());
        savedComplaint.setStatus("Відкрито");

        if (complaintDto.getCommentId() != null) {
            Comment comment = commentService.findById(complaintDto.getCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + complaintDto.getCommentId()));
            savedComplaint.setCommentId(comment.getId());
        }

        if (complaintDto.getStudentId() != null) {
            Student student = studentService.findById(complaintDto.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + complaintDto.getStudentId()));
            savedComplaint.setStudentId(student.getId());
        }

        return complaintRepository.save(savedComplaint);
    }

    @Transactional
    @Override
    public void delete(Complaint complaint) {
        complaintRepository.delete(complaint);
    }

    @Transactional
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
        return complaintRepository.findAll();
    }

    @Override
    public List<Complaint> findClosedComplaints() {
        return complaintRepository.findClosedComplaints();
    }
}