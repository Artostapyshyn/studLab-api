package com.artostapyshyn.studlabapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestDto {
    private Long id;
    private Long receiverId;
    private Long senderId;
    private String senderFirstName;
    private String senderLastName;
    private byte[] senderPhoto;
}
