package com.mayur.DesiCart.shop.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mayur.DesiCart.shop.auth.dtos.LoginRequest;
import com.mayur.DesiCart.shop.auth.service.TokenGenService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final TokenGenService tokenGenService;
    private final AuthenticationManager authenticationManager;


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(
                       loginRequest.getUsername(), loginRequest.getPassword()
               )
        );
        String token = tokenGenService.generateToken(authentication); // at this point we should get the real authentication token
        return ResponseEntity.ok(Map.of("token", token));
    }
}
