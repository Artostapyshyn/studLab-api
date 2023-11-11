package com.artostapyshyn.studlabapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentOfferEditDto {
    private String name;
    private String description;
    private String price;
    private byte[] photoBytes;
    private StudentDto provider;
}
