package com.minishop.minishopspring.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequestDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

}
