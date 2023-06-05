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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


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
                .requestMatchers("api/v1/auth/**").permitAll()
                .requestMatchers("api/v1/vacancies/*", "api/v1/comments/reply/*").hasAnyRole("ROLE_STUDENT", "ROLE_ADMIN", "ROLE_MODERATOR", "ROLE_UNIVERSITY_REPRESENTATIVE")
                .requestMatchers("api/v1/favourites/add-to-favorites/*", "api/v1/messages/mark-as-read/*").hasAnyRole("ROLE_STUDENT", "ROLE_ADMIN", "ROLE_MODERATOR", "ROLE_UNIVERSITY_REPRESENTATIVE")
                .requestMatchers("api/v1/student/personal-info/*", "api/v1/student/resumes/*", "api/v1/student/certificates/*").hasAnyRole("ROLE_STUDENT", "ROLE_ADMIN", "ROLE_MODERATOR", "ROLE_UNIVERSITY_REPRESENTATIVE")
                .requestMatchers("api/v1/events/popular/*", "api/v1/events/add/*","api/v1/events/newest/*", "api/v1/events/upcoming/*").hasAnyRole("ROLE_STUDENT", "ROLE_ADMIN", "ROLE_MODERATOR", "ROLE_UNIVERSITY_REPRESENTATIVE")
                .requestMatchers("/swagger-ui/**","/v3/api-docs/**", "/actuator/**").permitAll()
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}