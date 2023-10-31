package com.artostapyshyn.studlabapi.controller;

import com.artostapyshyn.studlabapi.dto.StudentDto;
import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.service.StudentService;
import com.artostapyshyn.studlabapi.service.WebSocketMessageService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.*;

@Controller
@AllArgsConstructor
public class SocketStudentController {

    private final WebSocketMessageService webSocketMessageService;

    private final StudentService studentService;

    private final ModelMapper modelMapper;

    @MessageMapping("/personal-info")
    public void getPersonalInfo(Authentication authentication) {
        Long studentId = studentService.getAuthStudentId(authentication);
        Optional<Student> student = studentService.findById(studentId);

        List<StudentDto> studentDtoList = student
                .map(stud -> Collections.singletonList(modelMapper.map(stud, StudentDto.class)))
                .orElse(Collections.emptyList());

        webSocketMessageService.sendPayloads(studentDtoList, "/topic/student-personal-info");
    }

    @MessageMapping(value = "/profile")
    public void getProfile(@Payload StudentDto student) {
        Student existingStudent = studentService.findByFirstAndLastName(student.getFirstName(), student.getLastName());

        if (existingStudent != null) {
            StudentDto studentDto = modelMapper.map(existingStudent, StudentDto.class);
            webSocketMessageService.sendPayloads(Collections.singletonList(studentDto), "/topic/student-profile");
        }
    }
}
