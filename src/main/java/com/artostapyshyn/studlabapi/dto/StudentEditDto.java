package com.artostapyshyn.studlabapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentEditDto {
    private Long id;
    private String lastName;
    private String firstName;
    private String major;
    private String course;
    private String city;
    private byte[] photoBytes;
    private String birthDate;
    private String universityName;
    private Set<String> interests;
}
