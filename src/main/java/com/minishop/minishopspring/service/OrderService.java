package com.minishop.minishopspring.service;

import com.minishop.minishopspring.dto.OrderResponseDTO;
import com.minishop.minishopspring.dto.UpdateOrderStatusRequestDTO;
import com.minishop.minishopspring.entity.Order;
import com.minishop.minishopspring.entity.OrderItem;
import com.minishop.minishopspring.entity.Product;
import com.minishop.minishopspring.entity.User;
import com.minishop.minishopspring.enums.OrderStatus;
import com.minishop.minishopspring.exception.OrderException;
import com.minishop.minishopspring.mapper.OrderMapper;
import com.minishop.minishopspring.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CurrentUserService currentUserService;


    public List<OrderResponseDTO> getMyOrders() {

        User user = currentUserService.getCurrentUser();

        List<Order> orders =
                orderRepository.findByUserId(user.getId());

        return orders.stream()
                .map(orderMapper::toResponseDTO)
                .toList();
    }

    public OrderResponseDTO getMyOrderById(Long orderId) {

        User user = currentUserService.getCurrentUser();

        Order order = orderRepository
                .findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() ->
                        new OrderException("Order not found")
                );

        return orderMapper.toResponseDTO(order);
    }

    public List<OrderResponseDTO> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public OrderResponseDTO updateStatus(
            Long orderId,
            UpdateOrderStatusRequestDTO request
    ) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderException("Order not found")
                );

        OrderStatus currentStatus = order.getStatus();
        OrderStatus newStatus = request.getStatus();

        if (!isValidTransition(currentStatus, newStatus)) {
            throw new OrderException(
                    "Cannot change order from "
                            + currentStatus
                            + " to "
                            + newStatus
            );
        }

        if (newStatus == OrderStatus.CANCELLED) {

            for (OrderItem item : order.getItems()) {

                Product product = item.getProduct();

                product.setQuantity(
                        product.getQuantity() + item.getQuantity()
                );
            }
        }

        order.setStatus(newStatus);

        return orderMapper.toResponseDTO(order);
    }

    private boolean isValidTransition(
            OrderStatus currentStatus,
            OrderStatus newStatus
    ) {

        return switch (currentStatus) {

            case PENDING ->
                    newStatus == OrderStatus.CONFIRMED
                            || newStatus == OrderStatus.CANCELLED;

            case CONFIRMED ->
                    newStatus == OrderStatus.SHIPPING
                            || newStatus == OrderStatus.CANCELLED;

            case SHIPPING ->
                    newStatus == OrderStatus.COMPLETED;

            case COMPLETED, CANCELLED ->
                    false;
        };
    }
}
