package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.repository.StudentRepository;
import com.artostapyshyn.studlabapi.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Override
    @CacheEvict("studentsSave")
    public Student save(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        studentRepository.deleteById(id);
    }

    @Override
    public Student findByFirstNameAndLastName(String firstName, String lastName) {
        return studentRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    @Override
    public int countByEnabled(boolean enabled) {
        return studentRepository.countByEnabled(true);
    }

    @Override
    @Cacheable("registrationData")
    public Map<String, Integer> getRegistrationData() {
        List<Student> students = studentRepository.findAll();

        int[] monthlyRegistrations = new int[12];

        for (Student student : students) {
            int month = student.getRegistrationDate().getMonthValue();
            monthlyRegistrations[month - 1]++;
        }

        Map<String, Integer> registrationData = new LinkedHashMap<>();
        registrationData.put("січень", monthlyRegistrations[0]);
        registrationData.put("лютий", monthlyRegistrations[1]);
        registrationData.put("березень", monthlyRegistrations[2]);
        registrationData.put("квітень", monthlyRegistrations[3]);
        registrationData.put("травень", monthlyRegistrations[4]);
        registrationData.put("червень", monthlyRegistrations[5]);
        registrationData.put("липень", monthlyRegistrations[6]);
        registrationData.put("серпень", monthlyRegistrations[7]);
        registrationData.put("вересень", monthlyRegistrations[8]);
        registrationData.put("жовтень", monthlyRegistrations[9]);
        registrationData.put("листопад", monthlyRegistrations[10]);
        registrationData.put("грудень", monthlyRegistrations[11]);

        return registrationData;
    }
}
