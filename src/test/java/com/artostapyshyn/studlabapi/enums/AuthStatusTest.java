package com.artostapyshyn.studlabapi.enums;

import org.junit.jupiter.api.Test;

import static com.artostapyshyn.studlabapi.enums.AuthStatus.ONLINE;
import static com.artostapyshyn.studlabapi.enums.AuthStatus.OFFLINE;
import static org.junit.jupiter.api.Assertions.*;

class AuthStatusTest {

    @Test
    void values() {
        AuthStatus[] authStatuses = AuthStatus.values();
        assertEquals(2, authStatuses.length);
        assertEquals(ONLINE, authStatuses[0]);
        assertEquals(OFFLINE, authStatuses[1]);
    }

    @Test
    void valueOf() {
        AuthStatus authStatus = AuthStatus.valueOf("ONLINE");
        assertEquals(ONLINE, authStatus);
    }
}