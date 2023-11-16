package com.artostapyshyn.studlabapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentServiceOfferDto {
    private Long id;
    private String description;
    private String name;
    private String price;
    private double rating;
    private byte[] photoBytes;
    private StudentDto provider;
    private String telegram;
    private String viber;
    private String whatsapp;
    private String phoneNumber;
}
