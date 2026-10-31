package com.minishop.minishopspring.service;

import com.minishop.minishopspring.dto.OrderResponseDTO;
import com.minishop.minishopspring.entity.CartItem;
import com.minishop.minishopspring.entity.Order;
import com.minishop.minishopspring.entity.Product;
import com.minishop.minishopspring.entity.User;
import com.minishop.minishopspring.enums.OrderStatus;
import com.minishop.minishopspring.enums.Role;
import com.minishop.minishopspring.exception.CartException;
import com.minishop.minishopspring.integration.BaseIntegrationTest;
import com.minishop.minishopspring.repository.CartItemRepository;
import com.minishop.minishopspring.repository.OrderRepository;
import com.minishop.minishopspring.repository.ProductRepository;
import com.minishop.minishopspring.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CheckoutServiceIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager entityManager;

    @MockitoBean
    private CurrentUserService currentUserService;

    @Test
    void shouldCheckoutSuccessfully(){
        // ARRANGE
        User john = new User();
        john.setUsername("john");
        john.setEmail("john@gmail.com");
        john.setPassword("123456");
        john.setRole(Role.CUSTOMER);

        john = userRepository.save(john);

        Product iphone = new Product();
        iphone.setName("iPhone 16");
        iphone.setPrice(new BigDecimal("20000000"));
        iphone.setQuantity(10);

        iphone = productRepository.save(iphone);

        CartItem cartItem =
                new CartItem(john, iphone, 3);

        cartItemRepository.save(cartItem);

        when(currentUserService.getCurrentUser())
                .thenReturn(john);

        // ACT
        OrderResponseDTO response =
                checkoutService.checkout();

        entityManager.flush();
        entityManager.clear();


        // ASSERT
        List<Order> orders =
                orderRepository.findByUserId(john.getId());

        assertEquals(1, orders.size());
        Order savedOrder = orders.get(0);
        assertEquals(OrderStatus.PENDING, savedOrder.getStatus());

        assertEquals(
                0,
                new BigDecimal("60000000")
                        .compareTo(savedOrder.getTotalAmount())
        );

        Product updatedProduct =
                productRepository.findById(iphone.getId())
                        .orElseThrow();

        assertEquals(7, updatedProduct.getQuantity());

        List<CartItem> remainingCartItems =
                cartItemRepository.findByUserId(john.getId());

        assertTrue(remainingCartItems.isEmpty());

    }

    @Test
    void shouldThrowWhenCartIsEmpty() {

        // ARRANGE
        User john = new User();
        john.setUsername("john");
        john.setEmail("john@gmail.com");
        john.setPassword("123456");
        john.setRole(Role.CUSTOMER);

        john = userRepository.save(john);

        when(currentUserService.getCurrentUser())
                .thenReturn(john);

        // ACT + ASSERT
        CartException exception = assertThrows(
                CartException.class,
                () -> checkoutService.checkout()
        );

        assertEquals("Cart is empty", exception.getMessage());

        // ASSERT DATABASE
        List<Order> orders =
                orderRepository.findByUserId(john.getId());

        assertEquals(0, orders.size());
    }

    @Test
    void shouldThrowWhenStockIsInsufficient(){
        // Arrange
        User john = new User();
        john.setUsername("john");
        john.setEmail("john@gmail.com");
        john.setPassword("123456");
        john.setRole(Role.CUSTOMER);

        john = userRepository.save(john);

        Product iphone = new Product();
        iphone.setName("iPhone 16");
        iphone.setPrice(new BigDecimal("20000000"));
        iphone.setQuantity(10);

        iphone = productRepository.save(iphone);

        CartItem cartItem =
                new CartItem(john, iphone, 11);

        cartItemRepository.save(cartItem);

        when(currentUserService.getCurrentUser()).thenReturn(john);

        // ACT + ASSERT
        CartException exception = assertThrows(
                CartException.class,
                () -> checkoutService.checkout()
        );

        assertEquals("Not enough stock for product: " + iphone.getName(), exception.getMessage());

        // ASSERT DATABASE
        Product updatedProduct = productRepository
                .findById(iphone.getId())
                .orElseThrow();

        assertEquals(10, updatedProduct.getQuantity());


    }
}