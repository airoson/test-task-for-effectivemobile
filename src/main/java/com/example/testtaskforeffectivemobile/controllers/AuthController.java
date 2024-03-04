package com.example.testtaskforeffectivemobile.controllers;

import com.example.testtaskforeffectivemobile.dtos.*;
import com.example.testtaskforeffectivemobile.services.ClientService;
import com.example.testtaskforeffectivemobile.services.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/api")
@Slf4j
@Tag(name="Аутентификация", description = "Методы для регистрации и получения токенов доступа")
public class AuthController {
    private ClientService clientService;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    public AuthController(ClientService clientService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.clientService = clientService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    @Operation(summary = "Регистрация пользователя", description = "Позволяет создать нового пользователя")
    public ResponseEntity<SignupResult> createClient(@RequestBody SignupRequest signupRequest){
        Long id = clientService.createClient(signupRequest);
        return ResponseEntity.ok(new SignupResult(id));
    }

    @PostMapping("/login")
    @Operation(summary = "Вход в аккаунт", description = "Позволяет обменять учетные данные на токены доступа")
    public ResponseEntity<TokensPair> loginClient(@RequestBody LoginRequest loginRequest){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword()));
        }catch(BadCredentialsException e){
            log.info("Login attempt with invalid credentials: {}", loginRequest);
            return ResponseEntity.status(401).build();
        }
        TokensPair tokensPair = jwtService.generateNewTokensPair(loginRequest.getLogin());
        return ResponseEntity.ok(tokensPair);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Обновить токены", description = "Позволяет обменять refresh token на новые access token и refresh token")
    public ResponseEntity<TokensPair> updateTokens(@RequestBody UpdateTokenRequest updateTokenRequest){
        TokensPair tokensPair = jwtService.regainAccessToken(updateTokenRequest.getRefreshToken());
        return ResponseEntity.ok(tokensPair);
    }

}
