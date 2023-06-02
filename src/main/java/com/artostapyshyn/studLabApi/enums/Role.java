package com.artostapyshyn.studLabApi.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_STUDENT,
    ROLE_ADMIN,
    ROLE_MODERATOR,
    ROLE_UNIVERSITY_REPRESENTATIVE;

    @Override
    public String getAuthority() {
        return name();
    }
}
