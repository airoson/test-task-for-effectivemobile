package com.example.testtaskforeffectivemobile.config;

import com.example.testtaskforeffectivemobile.entities.Client;
import com.example.testtaskforeffectivemobile.filters.TokenFilter;
import com.example.testtaskforeffectivemobile.services.ClientService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, TokenFilter filter) throws Exception{
        http.csrf(csrf -> csrf.disable()).formLogin(form -> form.disable())
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/signup", "/api/login", "/api/refresh", "/swagger-ui").permitAll().
                                anyRequest().permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(ClientService clientService){
        return (username) -> {
            Client client = clientService.getClientByLogin(username);
            if(client != null){
                return new User(
                        username, client.getPassword(), List.of()
                );
            }else throw new UsernameNotFoundException("Client with login %s not found.".formatted(username));
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }
}
