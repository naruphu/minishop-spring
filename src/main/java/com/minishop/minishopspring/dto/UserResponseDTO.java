package com.minishop.minishopspring.dto;

import com.minishop.minishopspring.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private Role role;
}
