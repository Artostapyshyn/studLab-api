package com.artostapyshyn.studlabapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDto {
    @NotNull
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String city;
    private String major;
    private String course;
    private byte[] photoBytes;
    private String universityName;
}
