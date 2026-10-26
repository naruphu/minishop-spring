package com.minishop.minishopspring.repository;

import com.minishop.minishopspring.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository
        extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByUserIdAndProductId(
            Long userId,
            Long productId
    );

    List<CartItem> findByUserId(Long userId);

    Optional<CartItem> findByIdAndUserId(
            Long id,
            Long userId
    );
}