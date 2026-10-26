package com.minishop.minishopspring.service;

import com.minishop.minishopspring.entity.User;
import com.minishop.minishopspring.exception.UserNotFoundException;
import com.minishop.minishopspring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found: " + username
                        )
                );
    }
}
