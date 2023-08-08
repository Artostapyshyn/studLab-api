package com.artostapyshyn.studlabapi.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventTypeTest {

    @Test
    void values_shouldReturnAllEventTypes() {
        EventType[] eventTypes = EventType.values();
        assertEquals(3, eventTypes.length);
        assertEquals(EventType.UNIVERSITY_EVENT, eventTypes[0]);
        assertEquals(EventType.PARTNER_EVENT, eventTypes[1]);
        assertEquals(EventType.GENERAL_EVENT, eventTypes[2]);
    }

    @Test
    void valueOf_withValidEventTypeName_shouldReturnEventType() {
        EventType eventType = EventType.valueOf("GENERAL_EVENT");
        assertEquals(EventType.GENERAL_EVENT, eventType);
    }

    @Test
    void valueOf_withInvalidEventTypeName_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> EventType.valueOf("GLOBAL_EVENT"));
    }
}
