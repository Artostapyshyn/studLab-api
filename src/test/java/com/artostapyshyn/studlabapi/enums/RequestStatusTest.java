package com.artostapyshyn.studlabapi.enums;

import org.junit.jupiter.api.Test;

import static com.artostapyshyn.studlabapi.enums.RequestStatus.*;
import static org.junit.jupiter.api.Assertions.*;


class RequestStatusTest {

    @Test
    void values() {
        RequestStatus [] requestStatuses = RequestStatus.values();
        assertEquals(3, requestStatuses.length);
        assertEquals(PENDING, requestStatuses[0]);
        assertEquals(ACCEPTED, requestStatuses[1]);
        assertEquals(DECLINED, requestStatuses[2]);
    }

    @Test
    void valueOf() {
        RequestStatus requestStatus = RequestStatus.valueOf("PENDING");
        assertEquals(PENDING, requestStatus);
    }
}