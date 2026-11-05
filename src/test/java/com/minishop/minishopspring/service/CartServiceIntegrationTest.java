package com.minishop.minishopspring.service;

import com.minishop.minishopspring.dto.AddCartItemRequestDTO;
import com.minishop.minishopspring.dto.UpdateCartItemRequestDTO;
import com.minishop.minishopspring.entity.CartItem;
import com.minishop.minishopspring.entity.Product;
import com.minishop.minishopspring.entity.User;
import com.minishop.minishopspring.enums.Role;
import com.minishop.minishopspring.exception.CartException;
import com.minishop.minishopspring.exception.ProductNotFoundException;
import com.minishop.minishopspring.integration.BaseIntegrationTest;
import com.minishop.minishopspring.repository.CartItemRepository;
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
class CartServiceIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @MockitoBean
    private CurrentUserService currentUserService;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldRejectAddingInactiveProductToCart() {

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
        iphone.setActive(false);

        iphone = productRepository.save(iphone);

        AddCartItemRequestDTO addCartItemRequestDTO = new AddCartItemRequestDTO();
        addCartItemRequestDTO.setProductId(iphone.getId());
        addCartItemRequestDTO.setQuantity(3);



        when(currentUserService.getCurrentUser())
                .thenReturn(john);

        // ACT
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> cartService.addToCart(addCartItemRequestDTO)
        );

        // Kiểm tra exception message
        assertEquals(
                "Product not found with id: " + iphone.getId(),
                exception.getMessage()
        );

        // ASSERT DATABASE: CartItem không được tạo
        List<CartItem> cartItems =
                cartItemRepository.findByUserId(john.getId());

        assertTrue(cartItems.isEmpty());
    }

    @Test
    void shouldRejectUpdatingQuantityOfInactiveProduct() {
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
        iphone.setActive(false);

        iphone = productRepository.save(iphone);

        CartItem cartItem =
                new CartItem(john, iphone, 3);

        cartItemRepository.save(cartItem);

        entityManager.flush();
        entityManager.clear();

        when(currentUserService.getCurrentUser())
                .thenReturn(john);

        UpdateCartItemRequestDTO updateCartItemRequestDTO = new UpdateCartItemRequestDTO();
        updateCartItemRequestDTO.setQuantity(5);

        // ACT + ASSERT
        CartException exception = assertThrows(
                CartException.class,
                () -> cartService.updateQuantity(
                        cartItem.getId(),
                        updateCartItemRequestDTO
                )
        );

        assertEquals(
                "Product is no longer available",
                exception.getMessage()
        );

        // ASSERT DATABASE
        entityManager.clear();

        CartItem savedCartItem = cartItemRepository
                .findById(cartItem.getId())
                .orElseThrow();

        assertEquals(3, savedCartItem.getQuantity());
    }
}