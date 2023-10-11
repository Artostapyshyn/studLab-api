package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.dto.EditDto;
import com.artostapyshyn.studlabapi.dto.SignUpDto;
import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.repository.StudentRepository;
import com.artostapyshyn.studlabapi.service.StudentService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

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
    public Student findByFirstNameAndLastName(String firstName, String lastName) {
        return studentRepository.findByFirstNameAndLastName(firstName, lastName);
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
    public void updateStudent(Student existingStudent, EditDto updatedStudent) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        modelMapper.map(updatedStudent, existingStudent);
    }

    @Override
    public void signUpStudent(SignUpDto signUpDto, Student existingStudent) {
        existingStudent.setFirstName(signUpDto.getFirstName());
        existingStudent.setLastName(signUpDto.getLastName());
        existingStudent.setHasNewMessages(false);
        existingStudent.setCity(signUpDto.getCity());
        existingStudent.setMajor(signUpDto.getMajor());
        existingStudent.setCourse(signUpDto.getCourse());
        existingStudent.setPhotoBytes(signUpDto.getPhotoBytes());
        existingStudent.setRegistrationDate(LocalDateTime.now());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(signUpDto.getPassword());
        existingStudent.setPassword(encodedPassword);

        studentRepository.save(existingStudent);
    }
}
