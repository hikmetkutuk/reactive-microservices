package com.microservice.reactive.apigateway.config;

import com.microservice.reactive.apigateway.security.JwtTokenAuthenticationFilter;
import com.microservice.reactive.apigateway.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private static final String[] AUTH_WHITELIST = {
            "/api/v1/user/register",
            "/api/v1/user/login"
    };

    private static final String[] AUTH_ADMIN = {
            "/api/v1/user/list/**",
            "/api/v1/user/delete/**"
    };

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http,
                                                JwtTokenProvider tokenProvider) {

        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(auth -> auth
                        .pathMatchers(AUTH_WHITELIST).permitAll()
                        .pathMatchers(AUTH_ADMIN).hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
                .addFilterAt(new JwtTokenAuthenticationFilter(tokenProvider), SecurityWebFiltersOrder.HTTP_BASIC)
                .build();
    }
}
