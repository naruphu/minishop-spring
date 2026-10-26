package com.minishop.minishopspring.mapper;

import com.minishop.minishopspring.dto.UserRegisterRequestDTO;
import com.minishop.minishopspring.dto.UserResponseDTO;
import com.minishop.minishopspring.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserRegisterRequestDTO request) {
        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        return user;
    }

    public UserResponseDTO toDTO(User user) {
        UserResponseDTO response = new UserResponseDTO();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        return response;
    }
}
