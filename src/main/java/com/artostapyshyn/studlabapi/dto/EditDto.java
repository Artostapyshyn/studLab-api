package com.artostapyshyn.studlabapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditDto {
    private Long id;
    private String lastName;
    private String firstName;
    private String major;
    private String course;
    private String city;
    private byte[] photoBytes;
}
