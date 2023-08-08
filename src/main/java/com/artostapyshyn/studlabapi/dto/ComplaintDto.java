package com.artostapyshyn.studlabapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintDto {
    private Long studentId;
    private Long commentId;
    private String complaintReason;
}