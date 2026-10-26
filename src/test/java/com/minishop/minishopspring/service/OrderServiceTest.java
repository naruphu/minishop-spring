package com.minishop.minishopspring.service;

import com.minishop.minishopspring.entity.OrderItem;
import com.minishop.minishopspring.entity.Product;
import com.minishop.minishopspring.exception.OrderException;
import com.minishop.minishopspring.mapper.OrderMapper;
import com.minishop.minishopspring.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.minishop.minishopspring.dto.OrderResponseDTO;
import com.minishop.minishopspring.dto.UpdateOrderStatusRequestDTO;
import com.minishop.minishopspring.entity.Order;
import com.minishop.minishopspring.enums.OrderStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldUpdateOrderFromPendingToConfirmed() {

        // ARRANGE
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        UpdateOrderStatusRequestDTO request =
                new UpdateOrderStatusRequestDTO();
        request.setStatus(OrderStatus.CONFIRMED);

        OrderResponseDTO response = new OrderResponseDTO(
                1L,
                null,
                OrderStatus.CONFIRMED,
                null,
                null
        );

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderMapper.toResponseDTO(order))
                .thenReturn(response);


        // ACT
        OrderResponseDTO result =
                orderService.updateStatus(1L, request);


        // ASSERT
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertEquals(OrderStatus.CONFIRMED, result.getStatus());

        // VERIFY
        verify(orderRepository).findById(1L);
        verify(orderMapper).toResponseDTO(order);
    }

    @Test
    void shouldThrowExceptionWhenChangingFromPendingToCompleted() {

        // ARRANGE
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        UpdateOrderStatusRequestDTO request =
                new UpdateOrderStatusRequestDTO();
        request.setStatus(OrderStatus.COMPLETED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));


        // ACT + ASSERT
        assertThrows(
                OrderException.class,
                () -> orderService.updateStatus(1L, request)
        );
    }

    @Test
    void shouldRestoreStockWhenOrderIsCancelled() {

        // ARRANGE
        Product product = new Product();
        product.setQuantity(10);

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(3);

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.addItem(orderItem);

        UpdateOrderStatusRequestDTO request =
                new UpdateOrderStatusRequestDTO();
        request.setStatus(OrderStatus.CANCELLED);

        OrderResponseDTO response = new OrderResponseDTO(
                1L,
                null,
                OrderStatus.CANCELLED,
                null,
                null
        );

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderMapper.toResponseDTO(order))
                .thenReturn(response);


        // ACT
        OrderResponseDTO result =
                orderService.updateStatus(1L, request);


        // ASSERT
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(13, product.getQuantity());
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {

        // ARRANGE
        UpdateOrderStatusRequestDTO request =
                new UpdateOrderStatusRequestDTO();
        request.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(999L))
                .thenReturn(Optional.empty());


        // ACT + ASSERT
        assertThrows(
                OrderException.class,
                () -> orderService.updateStatus(999L, request)
        );


        // VERIFY
        verify(orderRepository).findById(999L);

        verify(orderMapper, never()).toResponseDTO(any());
    }
}