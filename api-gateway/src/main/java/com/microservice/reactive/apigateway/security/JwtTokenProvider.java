package com.microservice.reactive.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;

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

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().verifyWith(this.secretKey).build()
                .parseSignedClaims(token).getPayload();

        Object authoritiesClaim = claims.get(AUTHORITIES_KEY);

        Collection<? extends GrantedAuthority> authorities = authoritiesClaim == null
                ? AuthorityUtils.NO_AUTHORITIES
                : AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim.toString());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().verifyWith(this.secretKey)
                    .build().parseSignedClaims(token);
            // parseClaimsJws will check expiration date. No need do here.
            log.info("expiration date: {}", claims.getPayload().getExpiration());
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("Invalid JWT token: {}", e.getMessage());
            log.trace("Invalid JWT token trace.", e);
        }
        return false;
    }
}
