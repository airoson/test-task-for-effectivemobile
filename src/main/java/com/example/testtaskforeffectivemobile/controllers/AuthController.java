package com.example.testtaskforeffectivemobile.controllers;

import com.example.testtaskforeffectivemobile.dtos.LoginRequest;
import com.example.testtaskforeffectivemobile.dtos.SignupRequest;
import com.example.testtaskforeffectivemobile.dtos.SignupResult;
import com.example.testtaskforeffectivemobile.dtos.TokensPair;
import com.example.testtaskforeffectivemobile.services.ClientService;
import com.example.testtaskforeffectivemobile.services.JwtService;
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
    public ResponseEntity<?> createClient(@RequestBody SignupRequest signupRequest){
        Long id = clientService.createClient(signupRequest);
        Map<String, Object> message = new TreeMap<>();
        message.put("id", id);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginClient(@RequestBody LoginRequest loginRequest){
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
    public ResponseEntity<?> updateTokens(@RequestBody Map<String, String> token){
        if(!token.containsKey("refresh_token")) return ResponseEntity.badRequest().build();
        TokensPair tokensPair = jwtService.regainAccessToken(token.get("refresh_token"));
        return ResponseEntity.ok(tokensPair);
    }

}
