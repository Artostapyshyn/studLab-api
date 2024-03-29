package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.dto.ComplaintDto;
import com.artostapyshyn.studlabapi.entity.*;
import com.artostapyshyn.studlabapi.exception.exceptions.ResourceNotFoundException;
import com.artostapyshyn.studlabapi.repository.ComplaintRepository;
import com.artostapyshyn.studlabapi.service.CommentService;
import com.artostapyshyn.studlabapi.service.ComplaintService;
import com.artostapyshyn.studlabapi.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
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
                    default -> throw new IllegalArgumentException("Invalid block duration");
                }
            }

            if (newComplaintData.isCloseComplaint()) {
                complaint.setStatus("Закрито");
                complaintRepository.save(complaint);
            }
        });
    }

    @Transactional
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
            author.setLastActiveDateTime(LocalDateTime.now());
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

    @Scheduled(fixedRate = 300000)
    public void removeExpiredTickets() {
        List<Complaint> complaints = complaintRepository.findAll();
            for(Complaint complaint : complaints){
                Long commentId = complaint.getCommentId();
                Comment comment = commentService.findById(commentId).orElse(null);

                if(comment == null){
                    complaintRepository.delete(complaint);
                }
            }
    }

    @Override
    public List<Complaint> findAll() {
        return complaintRepository.findAll();
    }

    @Override
    public Optional<Complaint> findById(Long id) {
        return complaintRepository.findById(id);
    }

    @Override
    public List<Complaint> findClosedComplaints() {
        return complaintRepository.findClosedComplaints();
    }

    @Override
    public List<Complaint> findOpenedComplaints() {
        return complaintRepository.findOpenedComplaints();
    }

    @Transactional
    @Override
    public Complaint save(Complaint complaint) {
        return complaintRepository.save(complaint);
    }
}