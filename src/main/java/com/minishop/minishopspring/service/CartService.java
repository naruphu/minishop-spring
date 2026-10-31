package com.minishop.minishopspring.service;

import com.minishop.minishopspring.dto.AddCartItemRequestDTO;
import com.minishop.minishopspring.dto.CartItemResponseDTO;
import com.minishop.minishopspring.dto.CartResponseDTO;
import com.minishop.minishopspring.dto.UpdateCartItemRequestDTO;
import com.minishop.minishopspring.entity.CartItem;
import com.minishop.minishopspring.entity.Product;
import com.minishop.minishopspring.entity.User;
import com.minishop.minishopspring.exception.CartException;
import com.minishop.minishopspring.exception.ProductNotFoundException;
import com.minishop.minishopspring.repository.CartItemRepository;
import com.minishop.minishopspring.repository.ProductRepository;
import com.minishop.minishopspring.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public CartItemResponseDTO addToCart(AddCartItemRequestDTO request) {

        User user = currentUserService.getCurrentUser();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + request.getProductId()
                        )
                );

        CartItem cartItem = cartItemRepository
                .findByUserIdAndProductId(
                        user.getId(),
                        product.getId()
                )
                .orElse(null);

        if (cartItem == null) {

            if (request.getQuantity() > product.getQuantity()) {
                throw new CartException(
                        "Only " + product.getQuantity() + " items available"
                );
            }

            cartItem = new CartItem(
                    user,
                    product,
                    request.getQuantity()
            );

        } else {

            int newQuantity =
                    cartItem.getQuantity() + request.getQuantity();

            if (newQuantity > product.getQuantity()) {
                throw new CartException(
                        "Only " + product.getQuantity() + " items available"
                );
            }

            cartItem.setQuantity(newQuantity);
        }

        CartItem savedItem =
                cartItemRepository.save(cartItem);

        return toResponseDTO(savedItem);
    }
    @Transactional
    public CartItemResponseDTO updateQuantity(
            Long cartItemId,
            UpdateCartItemRequestDTO request
    ) {

        User user = currentUserService.getCurrentUser();

        CartItem cartItem = cartItemRepository
                .findByIdAndUserId(cartItemId, user.getId())
                .orElseThrow(() ->
                        new CartException("Cart item not found")
                );

        if (request.getQuantity() > cartItem.getProduct().getQuantity()) {
            throw new CartException(
                    "Only " + cartItem.getProduct().getQuantity()
                            + " items available"
            );
        }

        cartItem.setQuantity(request.getQuantity());

        CartItem savedItem =
                cartItemRepository.save(cartItem);

        return toResponseDTO(savedItem);
    }
    @Transactional
    public void removeItem(Long cartItemId) {

        User user = currentUserService.getCurrentUser();

        CartItem cartItem = cartItemRepository
                .findByIdAndUserId(cartItemId, user.getId())
                .orElseThrow(() ->
                        new CartException("Cart item not found")
                );

        cartItemRepository.delete(cartItem);
    }
    @Transactional(readOnly = true)
    public CartResponseDTO getCart() {

        User user = currentUserService.getCurrentUser();

        List<CartItem> cartItems =
                cartItemRepository.findByUserId(user.getId());

        List<CartItemResponseDTO> items =
                cartItems.stream()
                        .map(this::toResponseDTO)
                        .toList();

        BigDecimal total = items.stream()
                .map(CartItemResponseDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponseDTO(items, total);
    }

    private CartItemResponseDTO toResponseDTO(CartItem item) {

        BigDecimal subtotal =
                item.getProduct()
                        .getPrice()
                        .multiply(
                                BigDecimal.valueOf(item.getQuantity())
                        );

        return new CartItemResponseDTO(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                subtotal
        );
    }
}