package com.artostapyshyn.studlabapi.dto;

import com.artostapyshyn.studlabapi.enums.MeetingType;
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
public class MeetingDto {
    private Long id;
    private String name;
    @JsonFormat(pattern="dd-MM HH:mm")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime date;
    private String venue;
    private String description;
    private MeetingType meetingType;
    private StudentDto author;
    private Set<StudentDto> participants;
    private String eventName;
}
