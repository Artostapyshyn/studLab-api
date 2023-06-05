package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.UserSession;
import com.artostapyshyn.studlabapi.repository.UserSessionRepository;
import com.artostapyshyn.studlabapi.service.UserSessionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserSessionServiceImpl implements UserSessionService {

    private final UserSessionRepository userSessionRepository;

    @Override
    public Optional<UserSession> findBySessionId(String sessionId) {
        return userSessionRepository.findBySessionId(sessionId);
    }

    @Override
    public UserSession save(UserSession userSession) {
        return userSessionRepository.save(userSession);
    }

    @Override
    public void delete(UserSession userSession) {
        userSessionRepository.delete(userSession);
    }
}
