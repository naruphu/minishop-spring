package com.minishop.minishopspring.dto;

import com.minishop.minishopspring.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusRequestDTO {

    @NotNull(message = "Status is required")
    private OrderStatus status;
}
