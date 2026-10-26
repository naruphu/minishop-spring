package com.minishop.minishopspring.security;


import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;


    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService
    ){
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }



    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {


        // 1. Lấy Authorization Header

        String authHeader = request.getHeader("Authorization");


        // 2. Nếu không có token thì cho request đi tiếp

        if(authHeader == null || !authHeader.startsWith("Bearer ")){

            filterChain.doFilter(request,response);
            return;
        }



        // 3. Lấy token thật (bỏ chữ Bearer)

        String jwt = authHeader.substring(7);



        // 4. Lấy username trong token

        String username = jwtService.extractUsername(jwt);



        // 5. Kiểm tra username tồn tại
        // và user chưa được authentication

        if(username != null
                && SecurityContextHolder.getContext()
                .getAuthentication() == null){



            // 6. Load user từ database

            UserDetails userDetails =
                    userDetailsService
                            .loadUserByUsername(username);



            // 7. Kiểm tra token hợp lệ

            if(jwtService.isTokenValid(jwt, userDetails)){



                // 8. Tạo Authentication

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );



                // 9. Lưu user vào SecurityContext

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

            }

        }



        // 10. Cho request đi tiếp

        filterChain.doFilter(request,response);

    }

}