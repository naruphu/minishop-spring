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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        return http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
                )
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(
                                        "/users/register",
                                        "/users/login"
                                ).permitAll()

                                // Chỉ ADMIN được tạo product
                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/products/**"
                                ).hasRole("ADMIN")

                                // Chỉ ADMIN được sửa product
                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/products/**"
                                ).hasRole("ADMIN")

                                // Chỉ ADMIN được xóa product
                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/products/**"
                                ).hasRole("ADMIN")

                                // Chỉ ADMIN được xem tất cả orders
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/orders"
                                ).hasRole("ADMIN")

                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/orders/*/status"
                                ).hasRole("ADMIN")

                                .anyRequest()
                                .authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}