package com.artostapyshyn.studlabapi.enums;

import org.junit.jupiter.api.Test;

import static com.artostapyshyn.studlabapi.enums.MeetingType.EVENT_BASED;
import static com.artostapyshyn.studlabapi.enums.MeetingType.PERSONAL;
import static org.junit.jupiter.api.Assertions.*;

class MeetingTypeTest {

    @Test
    void values() {
        MeetingType[] meetingTypes = MeetingType.values();
        assertEquals(2, meetingTypes.length);
        assertEquals(PERSONAL, meetingTypes[0]);
        assertEquals(EVENT_BASED, meetingTypes[1]);
    }

    @Test
    void valueOf() {
        MeetingType meetingType = MeetingType.valueOf("PERSONAL");
        assertEquals(PERSONAL, meetingType);
    }
}