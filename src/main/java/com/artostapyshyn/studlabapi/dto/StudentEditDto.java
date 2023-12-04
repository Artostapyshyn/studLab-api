package com.artostapyshyn.studlabapi.dto;

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
public class StudentEditDto {
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
    private String universityName;
    private Set<String> interests;
}
