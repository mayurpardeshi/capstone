package com.mayur.DesiCart.shop.auth.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
public class TokenGenService {
    private final JwtEncoder encoder;

    public TokenGenService(JwtEncoder encoder){
        this.encoder = encoder;
    }

    public String generateToken(Authentication authentication){
        Instant now = Instant.now();
        // get the roles and join them with spaces, because OAuth2 scopes are space separated
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(5, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();

        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}

/*
* Understanding:
* 1. User sends credentials → /login
* 2. Spring Security authenticates via AuthenticationManager
* 3. We receive a fully populated Authentication
* 4. We generate JWT from that authentication
* 5. Client uses JWT to protect API
* */
