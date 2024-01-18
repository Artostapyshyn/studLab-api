package com.artostapyshyn.studlabapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;

@Configuration
public class SecureRandomConfig {

    @Bean
    public SecureRandom random() {
        return new SecureRandom();
    }
}
