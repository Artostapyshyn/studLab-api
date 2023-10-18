package com.artostapyshyn.studlabapi.dto;

import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.enums.MeetingType;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class MeetingDto {
    private Long id;
    private String name;
    @JsonFormat(pattern="dd-MM HH:mm")
    private LocalDateTime date;
    private String venue;
    private String description;
    private MeetingType meetingType;
    private Student author;
    private Set<Student> participants;
}
