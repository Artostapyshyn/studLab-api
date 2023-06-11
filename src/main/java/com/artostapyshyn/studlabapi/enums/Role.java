package com.artostapyshyn.studlabapi.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_STUDENT,
    ROLE_ADMIN,
    ROLE_MODERATOR;

    @Override
    public String getAuthority() {
        return name();
    }
}
