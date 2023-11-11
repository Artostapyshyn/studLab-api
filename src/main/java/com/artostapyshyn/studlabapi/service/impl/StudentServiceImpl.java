package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.dto.StudentEditDto;
import com.artostapyshyn.studlabapi.dto.SignUpDto;
import com.artostapyshyn.studlabapi.entity.Major;
import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.entity.University;
import com.artostapyshyn.studlabapi.repository.MajorRepository;
import com.artostapyshyn.studlabapi.repository.StudentRepository;
import com.artostapyshyn.studlabapi.repository.UniversityRepository;
import com.artostapyshyn.studlabapi.service.StudentService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.artostapyshyn.studlabapi.enums.AuthStatus.OFFLINE;

@Service
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    private final UniversityRepository universityRepository;

    private final MajorRepository majorRepository;

    @Override
    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    public Student findByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    @Transactional
    @Override
    public Student save(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        studentRepository.deleteById(id);
    }

    @Override
    public List<Student> searchByNames(String firstName, String lastName) {
        if (firstName != null && lastName != null) {
            return studentRepository.findByFirstNameContainingAndLastNameContaining(firstName, lastName);
        } else if (firstName != null) {
            return studentRepository.findByFirstNameContaining(firstName);
        } else if (lastName != null) {
            return studentRepository.findByLastNameContaining(lastName);
        } else {
            return findAll();
        }
    }

    @Override
    public List<Student> findByFirstNameContainingAndLastNameContaining(String firstName, String lastName) {
        return studentRepository.findByFirstNameContainingAndLastNameContaining(firstName, lastName);
    }

    @Override
    public Student findByFirstAndLastName(String firstName, String lastName) {
        return studentRepository.findByFirstAndLastName(firstName, lastName);
    }

    @Override
    @Transactional
    public void updatePassword(Student student, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        student.setPassword(encodedPassword);
        studentRepository.save(student);
    }

    @Override
    public Long getAuthStudentId(Authentication authentication) {
        String studentEmail = authentication.getName();
        Student student = studentRepository.findByEmail(studentEmail);
        return student.getId();
    }

    @Transactional
    @Override
    public void updateStudent(Student existingStudent, StudentEditDto updatedStudent) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(updatedStudent, existingStudent);
    }

    @Transactional
    @Override
    public void signUpStudent(SignUpDto signUpDto, Student existingStudent) {
        updateStudentDetails(existingStudent, signUpDto);
        setMajor(existingStudent,signUpDto.getMajor());
        setUniversity(existingStudent, signUpDto.getUniversityName());
        existingStudent.setPassword(encodePassword(signUpDto.getPassword()));
        studentRepository.save(existingStudent);
    }

    private void updateStudentDetails(Student student, SignUpDto signUpDto) {
        student.setFirstName(signUpDto.getFirstName());
        student.setLastName(signUpDto.getLastName());
        student.setHasNewMessages(false);
        student.setCity(signUpDto.getCity());
        student.setCourse(signUpDto.getCourse());
        student.setPhotoBytes(signUpDto.getPhotoBytes());
        student.setRegistrationDate(LocalDateTime.now());
    }

    private void setUniversity(Student student, String universityName) {
        if(universityName != null) {
            University university = universityRepository.findByName(universityName);
            student.setUniversity(university);
        }
    }

    private void setMajor(Student student, String majorName) {
        if(majorName != null) {
            Major major = majorRepository.findByName(majorName);
            student.setMajor(major.getName());
        }
    }

    private String encodePassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    @Scheduled(fixedRate = 300000)
    public void updateOfflineUsersStatus() {
        LocalDateTime inactiveThreshold = LocalDateTime.now().minusMinutes(30);

        List<Student> inactiveStudents = studentRepository.findInactiveStudentsSince(inactiveThreshold);

        inactiveStudents.forEach(student -> {
            student.setAuthStatus(OFFLINE);
            studentRepository.save(student);
        });
    }
}
