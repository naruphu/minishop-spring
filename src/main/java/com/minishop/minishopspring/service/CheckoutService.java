package com.minishop.minishopspring.service;

import com.minishop.minishopspring.dto.OrderResponseDTO;
import com.minishop.minishopspring.entity.*;
import com.minishop.minishopspring.enums.OrderStatus;
import com.minishop.minishopspring.exception.CartException;
import com.minishop.minishopspring.exception.ProductNotFoundException;
import com.minishop.minishopspring.mapper.OrderMapper;
import com.minishop.minishopspring.repository.CartItemRepository;
import com.minishop.minishopspring.repository.OrderRepository;
import com.minishop.minishopspring.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;
    private final CurrentUserService currentUserService;


    @Transactional
    public OrderResponseDTO checkout() {

        User user = currentUserService.getCurrentUser();

        List<CartItem> cartItems =
                cartItemRepository.findByUserId(user.getId());

        if (cartItems.isEmpty()) {
            throw new CartException("Cart is empty");
        }
        List<Product> lockedProducts = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = productRepository
                    .findByIdForUpdate(cartItem.getProduct().getId())
                    .orElseThrow(() ->
                            new ProductNotFoundException("Product not found")
                    );


            if (cartItem.getQuantity() > product.getQuantity()) {
                throw new CartException(
                        "Not enough stock for product: " + product.getName()
                );
            }

            lockedProducts.add(product);
        }

        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < cartItems.size(); i++) {

            CartItem cartItem = cartItems.get(i);
            Product product = lockedProducts.get(i);

            BigDecimal subtotal =
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(cartItem.getQuantity())
                            );

            total = total.add(subtotal);
        }

        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        for (int i = 0; i < cartItems.size(); i++) {

            CartItem cartItem = cartItems.get(i);
            Product product = lockedProducts.get(i);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());

            order.addItem(orderItem);

            product.setQuantity(
                    product.getQuantity() - cartItem.getQuantity()
            );
        }

        Order savedOrder = orderRepository.save(order);

        cartItemRepository.deleteAll(cartItems);

        // còn thiếu đúng đoạn này
        return orderMapper.toResponseDTO(savedOrder);
    }


}
