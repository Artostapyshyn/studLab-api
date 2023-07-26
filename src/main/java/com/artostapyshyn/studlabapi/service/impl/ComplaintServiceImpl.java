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
    public Complaint saveComplaint(Complaint complaint) {
        Complaint savedComplaint = new Complaint();
        savedComplaint.setComplaintReason(complaint.getComplaintReason());
        savedComplaint.setStatus("Відкрито");
        savedComplaint.setDeleteComment(complaint.isDeleteComment());
        savedComplaint.setBlockUser(complaint.isBlockUser());
        savedComplaint.setBlockDuration(complaint.getBlockDuration());
        savedComplaint.setCloseComplaint(complaint.isCloseComplaint());

        if (complaint.getCommentId() != null) {
            Comment comment = commentService.findById(complaint.getCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + complaint.getCommentId()));
            savedComplaint.setCommentId(comment.getId());
        }

        if (complaint.getStudentId() != null) {
            Student student = studentService.findById(complaint.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + complaint.getStudentId()));
            savedComplaint.setStudentId(student.getId());
        }
        return complaintRepository.save(savedComplaint);
    }

    @Override
    public Complaint convertDtoToComplaint(ComplaintDto complaintDto) {
        return modelMapper.map(complaintDto, Complaint.class);
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