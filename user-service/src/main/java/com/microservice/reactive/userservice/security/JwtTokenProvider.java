package com.microservice.reactive.userservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

import static java.util.stream.Collectors.joining;

@Component
public class JwtTokenProvider {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    private static final String AUTHORITIES_KEY = "roles";
    private SecretKey secretKey;

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @PostConstruct
    public void init() {
        var secret = Base64.getEncoder()
                .encodeToString(SECRET_KEY.getBytes());
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(Authentication authentication) {

        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        var claimsBuilder = Jwts.claims().subject(username);
        if (!authorities.isEmpty()) {
            claimsBuilder.add(AUTHORITIES_KEY, authorities.stream()
                    .map(GrantedAuthority::getAuthority).collect(joining(",")));
        }

        var claims = claimsBuilder.build();

        Date now = new Date();
        // validity in milliseconds
        // 1h
        long validityInMs = 3600000;
        Date validity = new Date(now.getTime() + validityInMs);

        return Jwts.builder().claims(claims).issuedAt(now).expiration(validity)
                .signWith(this.secretKey, Jwts.SIG.HS256).compact();

    }
}
