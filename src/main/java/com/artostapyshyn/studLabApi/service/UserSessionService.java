package com.artostapyshyn.studLabApi.service;

import com.artostapyshyn.studLabApi.entity.UserSession;

import java.util.Optional;

public interface UserSessionService {

    Optional<UserSession> findBySessionId(String sessionId);

    UserSession save(UserSession userSession);

    void delete(UserSession userSession);
}
