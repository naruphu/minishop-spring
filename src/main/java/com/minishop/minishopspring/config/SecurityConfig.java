package com.minishop.minishopspring.config;

import com.minishop.minishopspring.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // AUTH - PUBLIC
                        // =========================
                        .requestMatchers(
                                "/users/register",
                                "/users/login"
                        ).permitAll()


                        // =========================
                        // PRODUCT - PUBLIC
                        // =========================
                        .requestMatchers(
                                HttpMethod.GET,
                                "/products",
                                "/products/**"
                        ).permitAll()


                        // =========================
                        // ADMIN ROUTES
                        // =========================
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")


                        // =========================
                        // USER MANAGEMENT - ADMIN
                        // =========================
                        .requestMatchers(
                                "/users",
                                "/users/**"
                        ).hasRole("ADMIN")


                        // =========================
                        // ORDER MANAGEMENT - ADMIN
                        // =========================
                        .requestMatchers(
                                HttpMethod.GET,
                                "/orders"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/orders/*/status"
                        ).hasRole("ADMIN")


                        // =========================
                        // CATEGORY MANAGEMENT - ADMIN
                        // =========================
                        .requestMatchers(
                                HttpMethod.POST,
                                "/categories"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/categories/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/categories/**"
                        ).hasRole("ADMIN")


                        // =========================
                        // OTHER REQUESTS
                        // =========================
                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }
}