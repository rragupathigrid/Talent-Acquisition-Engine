package com.forge.talentacquisitionengine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Development Security Configuration
 *
 * Permits all requests without authentication for local development.
 * Google OAuth2 credentials are not configured locally — this bypasses
 * the OAuth2 login redirect so APIs can be tested directly via Postman.
 *
 * TODO: For production, replace with proper JWT/OAuth2 resource server config
 * using CH T1's auth service.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for REST API (stateless)
            .csrf(AbstractHttpConfigurer::disable)

            // Allow all requests without authentication for local dev
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )

            // Disable the default OAuth2 login page (removes Google redirect)
            .oauth2Login(AbstractHttpConfigurer::disable)

            // Disable form login
            .formLogin(AbstractHttpConfigurer::disable)

            // Disable HTTP Basic auth
            .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
