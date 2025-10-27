package com.shashi.apigateway.config;

import com.shashi.apigateway.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .authorizeExchange(auth -> auth
                        // Auth endpoints
                        .pathMatchers("/auth-service/api/auth/**").permitAll()

                        .pathMatchers(HttpMethod.OPTIONS).permitAll()

                        // Actuator endpoints
                        .pathMatchers("/actuator/health", "/actuator/info").permitAll()

                        // Swagger UI resources
                        .pathMatchers("/swagger-ui/**").permitAll()
                        .pathMatchers("/swagger-ui.html").permitAll()
                        .pathMatchers("/webjars/**").permitAll()
                        .pathMatchers("/swagger-resources/**").permitAll()

                        // API Gateway's own API docs
                        .pathMatchers("/v3/api-docs/**").permitAll()
                        .pathMatchers("/v3/api-docs").permitAll()

                        // Microservice Swagger UI accessed through gateway
                        .pathMatchers("/*/swagger-ui/**").permitAll()
                        .pathMatchers("/*/swagger-ui.html").permitAll()

                        // ⭐ CRITICAL: Microservice API docs through gateway
                        .pathMatchers("/*/v3/api-docs").permitAll()
                        .pathMatchers("/*/v3/api-docs/**").permitAll()

                        // Explicitly allow each microservice's API docs
                        .pathMatchers(
                                "/analytics-service/v3/api-docs",
                                "/auth-service/v3/api-docs",
                                "/energy-service/v3/api-docs",
                                "/food-service/v3/api-docs",
                                "/leaderboard-service/v3/api-docs",
                                "/transport-service/v3/api-docs"
                        ).permitAll()

                        // All other requests require authentication
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
