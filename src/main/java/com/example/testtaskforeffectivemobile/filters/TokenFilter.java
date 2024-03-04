package com.example.testtaskforeffectivemobile.filters;

import com.example.testtaskforeffectivemobile.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class TokenFilter extends OncePerRequestFilter {
    private JwtService jwtService;

    public TokenFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            String token = authHeader.substring(7);
            String subject = jwtService.verifyAccessToken(token);
            if(subject != null){
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                        subject, null, List.of()
                ));
            }else{
                log.info("Not valid access token send with request.");
            }
        }
        filterChain.doFilter(request, response);
    }
}
