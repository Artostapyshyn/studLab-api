package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.UserSession;

import java.util.Optional;

public interface UserSessionService {

    Optional<UserSession> findBySessionId(String sessionId);

    UserSession save(UserSession userSession);

    void delete(UserSession userSession);
}
