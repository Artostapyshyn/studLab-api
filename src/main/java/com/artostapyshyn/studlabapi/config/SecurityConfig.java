package com.artostapyshyn.studlabapi.config;

import com.artostapyshyn.studlabapi.util.JwtRequestFilter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("api/v1/**").permitAll()
                .requestMatchers("api/v1/vacancies/**", "api/v1/comments/**").hasAnyRole("ROLE_STUDENT", "ROLE_ADMIN", "ROLE_MODERATOR")
                .requestMatchers("api/v1/favourites/**", "api/v1/messages/**").hasAnyRole("ROLE_STUDENT", "ROLE_ADMIN", "ROLE_MODERATOR")
                .requestMatchers("api/v1/student/**", "api/v1/complaints/**").hasAnyRole("ROLE_STUDENT", "ROLE_ADMIN", "ROLE_MODERATOR")
                .requestMatchers("api/v1/events/**", "api/v1/course/**").hasAnyRole("ROLE_STUDENT", "ROLE_ADMIN", "ROLE_MODERATOR")
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").hasAnyRole("ROLE_ADMIN")
                .requestMatchers("api/v1/statistic/**").hasAnyRole("ROLE_ADMIN")
                .anyRequest()
                .authenticated().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}