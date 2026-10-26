package com.minishop.minishopspring.dto;

import com.minishop.minishopspring.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDTO {
    public String token;
    public String username;
    public Role role;
}
