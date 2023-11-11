package com.artostapyshyn.studlabapi.dto;

import com.artostapyshyn.studlabapi.entity.Tag;
import com.artostapyshyn.studlabapi.enums.EventType;
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
public class EventDto {
    private Long id;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime date;
    @JsonFormat(pattern="dd-MM HH:mm")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endDate;
    private String venue;
    private EventType eventType;
    private String description;
    private String nameOfEvent;
    private byte[] eventPhoto;
    private int favoriteCount;
    private LocalDateTime creationDate;
    private Set<Tag> tags;
}
