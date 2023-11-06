package com.artostapyshyn.studlabapi.dto;
import com.artostapyshyn.studlabapi.entity.University;
import com.artostapyshyn.studlabapi.enums.AuthStatus;
import com.artostapyshyn.studlabapi.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentDto {
    private Long id;
    private String lastName;
    private String firstName;
    private String major;
    private String course;
    private String city;
    private byte[] photoBytes;
    private University university;
    private String email;
    private Role role;
    private AuthStatus authStatus;
    private LocalDateTime lastActiveDateTime;
}
