package com.artostapyshyn.studlabapi.dto;
import com.artostapyshyn.studlabapi.entity.University;
import com.artostapyshyn.studlabapi.enums.AuthStatus;
import com.artostapyshyn.studlabapi.enums.Role;
import com.artostapyshyn.studlabapi.util.CustomLocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

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
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDateTime birthDate;
    private University university;
    private String email;
    private Role role;
    private AuthStatus authStatus;
    private LocalDateTime lastActiveDateTime;
    private Set<StudentInterestDto> interests;
}
