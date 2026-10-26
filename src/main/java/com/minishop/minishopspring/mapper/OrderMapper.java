package com.minishop.minishopspring.mapper;

import com.minishop.minishopspring.dto.OrderItemResponseDTO;
import com.minishop.minishopspring.dto.OrderResponseDTO;
import com.minishop.minishopspring.entity.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderMapper {

    public OrderResponseDTO toResponseDTO(Order order) {

        List<OrderItemResponseDTO> items =
                order.getItems()
                        .stream()
                        .map(item -> {

                            BigDecimal subtotal =
                                    item.getPrice()
                                            .multiply(
                                                    BigDecimal.valueOf(item.getQuantity())
                                            );

                            return new OrderItemResponseDTO(
                                    item.getId(),
                                    item.getProduct().getId(),
                                    item.getProduct().getName(),
                                    item.getPrice(),
                                    item.getQuantity(),
                                    subtotal
                            );
                        })
                        .toList();

        return new OrderResponseDTO(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                items
        );
    }
}
