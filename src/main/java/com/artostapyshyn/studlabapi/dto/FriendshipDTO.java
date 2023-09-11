package com.artostapyshyn.studlabapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipDTO {
    private Long id;
    private Long studentId;
    private Long friendId;
    private String friendFirstName;
    private String friendLastName;
    private byte[] friendPhoto;
}