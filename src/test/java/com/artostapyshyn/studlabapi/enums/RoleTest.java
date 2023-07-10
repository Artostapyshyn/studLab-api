package com.artostapyshyn.studlabapi.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void getAuthority_shouldReturnRoleName() {
        Role role = Role.ROLE_STUDENT;
        String authority = role.getAuthority();
        assertEquals("ROLE_STUDENT", authority);
    }

    @Test
    void values_shouldReturnAllRoles() {
        Role[] roles = Role.values();
        assertEquals(3, roles.length);
        assertEquals(Role.ROLE_STUDENT, roles[0]);
        assertEquals(Role.ROLE_ADMIN, roles[1]);
        assertEquals(Role.ROLE_MODERATOR, roles[2]);
    }

    @Test
    void valueOf_withValidRoleName_shouldReturnRole() {
        Role role = Role.valueOf("ROLE_ADMIN");
        assertEquals(Role.ROLE_ADMIN, role);
    }

    @Test
    void valueOf_withInvalidRoleName_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Role.valueOf("ROLE_UNKNOWN"));
    }
}