package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.dto.ComplaintDto;
import com.artostapyshyn.studlabapi.entity.*;
import com.artostapyshyn.studlabapi.exception.exceptions.ResourceNotFoundException;
import com.artostapyshyn.studlabapi.repository.ComplaintRepository;
import com.artostapyshyn.studlabapi.service.CommentService;
import com.artostapyshyn.studlabapi.service.ComplaintService;
import com.artostapyshyn.studlabapi.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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

    @Transactional
    @Override
    public void processComplaints(Complaint newComplaintData) {
        Optional<Complaint> existingComplaint = complaintRepository.findById(newComplaintData.getId());

        existingComplaint.ifPresent(complaint -> {
            Student student = studentService.findById(newComplaintData.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
            Comment comment = commentService.findById(newComplaintData.getCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

            if (newComplaintData.isDeleteComment()) {
                commentService.delete(comment);
            }

            if (newComplaintData.isBlockUser()) {
                switch (newComplaintData.getBlockDuration()) {
                    case "week" -> student.setBlockedUntil(LocalDateTime.now().plusWeeks(1));
                    case "month" -> student.setBlockedUntil(LocalDateTime.now().plusMonths(1));
                    case "forever" -> student.setBlockedUntil(LocalDateTime.now().plusYears(10));
                }
            }

            if (newComplaintData.isCloseComplaint()) {
                complaint.setStatus("Закрито");
                complaintRepository.save(complaint);
            }
        });
    }

    @Transactional
    @CachePut(value = {"allComplaints", "complaintById", "closedComplaints"})
    @Override
    public Complaint saveComplaint(ComplaintDto complaintDto) {
        Complaint savedComplaint = new Complaint();
        savedComplaint.setComplaintReason(complaintDto.getComplaintReason());
        savedComplaint.setStatus("Відкрито");

        if (complaintDto.getCommentId() != null) {
            Comment comment = commentService.findById(complaintDto.getCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + complaintDto.getCommentId()));
            savedComplaint.setCommentId(comment.getId());

            Student author = studentService.findById(comment.getStudent().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + comment.getStudent().getId()));
            savedComplaint.setStudentId(author.getId());
        }

        if (complaintDto.getStudentId() != null) {
            Student student = studentService.findById(complaintDto.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + complaintDto.getStudentId()));
            savedComplaint.setStudentId(student.getId());
        }

        return complaintRepository.save(savedComplaint);
    }

    @Transactional
    @CacheEvict(value = {"allComplaints", "complaintById", "closedComplaints"})
    @Override
    public void delete(Complaint complaint) {
        complaintRepository.delete(complaint);
    }

    @Override
    @Cacheable(value = "allComplaints")
    public List<Complaint> findAll() {
        return complaintRepository.findAll();
    }

    @Override
    @Cacheable(value = "complaintById")
    public Optional<Complaint> findById(Long id) {
        return complaintRepository.findById(id);
    }

    @Override
    @Cacheable(value = "closedComplaints")
    public List<Complaint> findClosedComplaints() {
        return complaintRepository.findClosedComplaints();
    }

    @Transactional
    @CachePut(value = {"allComplaints", "complaintById", "closedComplaints"})
    @Override
    public Complaint save(Complaint complaint) {
        return complaintRepository.save(complaint);
    }
}