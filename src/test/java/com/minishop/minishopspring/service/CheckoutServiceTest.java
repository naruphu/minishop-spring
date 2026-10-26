package com.minishop.minishopspring.service;

import com.minishop.minishopspring.dto.OrderResponseDTO;
import com.minishop.minishopspring.entity.*;
import com.minishop.minishopspring.enums.OrderStatus;
import com.minishop.minishopspring.exception.CartException;
import com.minishop.minishopspring.mapper.OrderMapper;
import com.minishop.minishopspring.repository.CartItemRepository;
import com.minishop.minishopspring.repository.OrderRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private CheckoutService checkoutService;

    @Test
    void shouldThrowExceptionWhenCartIsEmpty() {

        // ARRANGE
        User user = new User();
        user.setId(1L);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(cartItemRepository.findByUserId(1L))
                .thenReturn(List.of());


        // ACT + ASSERT
        assertThrows(
                CartException.class,
                () -> checkoutService.checkout()
        );


        // VERIFY
        verify(currentUserService).getCurrentUser();
        verify(cartItemRepository).findByUserId(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenStockIsNotEnough() {

        // ARRANGE
        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setName("iPhone");
        product.setQuantity(5);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(6);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(cartItemRepository.findByUserId(1L))
                .thenReturn(List.of(cartItem));


        // ACT + ASSERT
        assertThrows(
                CartException.class,
                () -> checkoutService.checkout()
        );


        // ASSERT
        assertEquals(5, product.getQuantity());


        // VERIFY
        verify(currentUserService).getCurrentUser();
        verify(cartItemRepository).findByUserId(1L);

        verify(orderRepository, never()).save(any());
        verify(cartItemRepository, never()).deleteAll(any());
    }

    @Test
    void shouldCheckoutSuccessfully() {

        // ARRANGE
        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(10L);
        product.setName("iPhone");
        product.setPrice(new BigDecimal("100"));
        product.setQuantity(10);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(3);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(cartItemRepository.findByUserId(1L))
                .thenReturn(List.of(cartItem));


        // Khi save bất kỳ Order nào
        // → trả lại chính Order được truyền vào
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        OrderResponseDTO response = new OrderResponseDTO(
                1L,
                new BigDecimal("300"),
                OrderStatus.PENDING,
                null,
                null
        );

        when(orderMapper.toResponseDTO(any(Order.class)))
                .thenReturn(response);


        // ACT
        OrderResponseDTO result = checkoutService.checkout();


        // ASSERT
        assertEquals(new BigDecimal("300"), result.getTotalAmount());
        assertEquals(OrderStatus.PENDING, result.getStatus());

        assertEquals(7, product.getQuantity());


        // CAPTURE ORDER
        ArgumentCaptor<Order> orderCaptor =
                ArgumentCaptor.forClass(Order.class);

        verify(orderRepository)
                .save(orderCaptor.capture());

        Order capturedOrder =
                orderCaptor.getValue();


// ASSERT SAVED ORDER
        assertEquals(
                new BigDecimal("300"),
                capturedOrder.getTotalAmount()
        );

        assertEquals(
                OrderStatus.PENDING,
                capturedOrder.getStatus()
        );

        assertEquals(user, capturedOrder.getUser());

        assertEquals(1, capturedOrder.getItems().size());


    // ASSERT ORDER ITEM
            OrderItem capturedItem =
                    capturedOrder.getItems().get(0);

            assertEquals(product, capturedItem.getProduct());
            assertEquals(3, capturedItem.getQuantity());

            assertEquals(
                    new BigDecimal("100"),
                    capturedItem.getPrice()
            );


    // VERIFY
            verify(cartItemRepository).deleteAll(any());
            verify(orderMapper).toResponseDTO(any(Order.class));
    }
}