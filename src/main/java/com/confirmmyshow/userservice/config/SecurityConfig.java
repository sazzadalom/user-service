package com.confirmmyshow.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .formLogin(form -> form.disable())

            .httpBasic(basic -> basic.disable())

            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth

                // Public APIs
                .requestMatchers(HttpMethod.POST,
                    "/api/v1/auth/register",
                    "/api/v1/auth/login",
                    "/api/v1/auth/refresh",
                    "/api/v1/auth/logout"
                ).permitAll()

                .requestMatchers(HttpMethod.GET,
                		"/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**").permitAll()
                
                .requestMatchers("/.well-known/jwks.json")
                .permitAll()

                // Admin APIs
                .requestMatchers("/api/v1/admin/**")
                .hasRole("ADMIN")

                // Protected user APIs
                .requestMatchers("/api/v1/users/**")
                .authenticated()

                // Internal APIs remain closed until their
                // authentication mechanism is defined.
                .requestMatchers("/internal/v1/**")
                .denyAll()

                .anyRequest().denyAll()
            );

        return http.build();
    }
}