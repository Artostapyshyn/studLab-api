package com.artostapyshyn.studlabapi.util;

import com.artostapyshyn.studlabapi.service.impl.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
@AllArgsConstructor
@Log4j2
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String tokenHeader = request.getHeader("Authorization");
        if (StringUtils.isBlank(tokenHeader) || !tokenHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String jwtToken = tokenHeader.substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
        if (StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            authenticateUser(jwtToken, userDetails, request);
        }
        chain.doFilter(request, response);
    }

    private void authenticateUser(String jwtToken, UserDetails userDetails, HttpServletRequest request) {
        if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }
}
