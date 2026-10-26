package com.minishop.minishopspring.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequestDTO {
    @NotBlank(message = "Category name must not be blank")
    private String name;
}
