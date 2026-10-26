package com.minishop.minishopspring.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class CartResponseDTO {

    private List<CartItemResponseDTO> items;

    private BigDecimal total;
}