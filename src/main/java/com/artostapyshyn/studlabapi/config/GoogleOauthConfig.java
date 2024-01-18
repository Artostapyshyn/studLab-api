package com.artostapyshyn.studlabapi.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
public class GoogleOauthConfig {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier() throws GeneralSecurityException, IOException {
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        return new GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();
    }
}
