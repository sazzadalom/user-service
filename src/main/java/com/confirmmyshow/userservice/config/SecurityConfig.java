
package com.confirmmyshow.userservice.config;

import com.confirmmyshow.userservice.filter.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .formLogin(form -> form.disable())

            .httpBasic(basic -> basic.disable())

            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            .exceptionHandling(exception -> exception
                    .authenticationEntryPoint((request, response, ex) ->
                            response.sendError(
                                    HttpServletResponse.SC_UNAUTHORIZED
                            )
                    )
            )

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                        HttpMethod.POST,
                        "/api/v1/auth/register",
                        "/api/v1/auth/login",
                        "/api/v1/auth/refresh",
                        "/api/v1/auth/logout"
                ).permitAll()

                .requestMatchers(
                        HttpMethod.GET,
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**"
                ).permitAll()

                .requestMatchers("/.well-known/jwks.json")
                .permitAll()

                .requestMatchers("/api/v1/admin/**")
                .hasRole("ADMIN")

                .requestMatchers("/api/v1/users/**")
                .authenticated()

                .requestMatchers("/internal/v1/**")
                .denyAll()

                .anyRequest().denyAll()
            )

            // Register JWT filter before username/password authentication.
            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
