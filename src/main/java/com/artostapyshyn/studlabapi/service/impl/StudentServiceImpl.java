package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.repository.StudentRepository;
import com.artostapyshyn.studlabapi.service.StudentService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Override
    @Cacheable("studentsById")
    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    @Cacheable("studentsByEmail")
    public Student findByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    @Transactional
    @Override
    @CacheEvict("studentsSave")
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
    public int countByEnabled(boolean enabled) {
        return studentRepository.countByEnabled(true);
    }

    @Override
    @Cacheable("registrationData")
    public Map<String, Integer> getRegistrationData() {
        List<Student> students = studentRepository.findAll();

        Map<String, Integer> registrationData = students.stream()
                .collect(Collectors.groupingBy(
                        student -> student.getRegistrationDate().getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
                        Collectors.summingInt(student -> 1)
                ));

        for (Month month : Month.values()) {
            String monthName = month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());
            registrationData.putIfAbsent(monthName, 0);
        }

        return registrationData;
    }

    @Override
    public Long getAuthStudentId(Authentication authentication) {
        String studentEmail = authentication.getName();
        Student student = studentRepository.findByEmail(studentEmail);
        return student.getId();
    }

    @Transactional
    @Override
    public void updateStudent(Student existingStudent, Student updatedStudent) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        modelMapper.map(updatedStudent, existingStudent);

        if (updatedStudent.getPassword() != null) {
            String hashedPassword = new BCryptPasswordEncoder().encode(updatedStudent.getPassword());
            existingStudent.setPassword(hashedPassword);
        }
    }
}
