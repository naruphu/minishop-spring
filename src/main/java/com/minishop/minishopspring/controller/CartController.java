package com.minishop.minishopspring.controller;

import com.minishop.minishopspring.dto.AddCartItemRequestDTO;
import com.minishop.minishopspring.dto.CartItemResponseDTO;
import com.minishop.minishopspring.dto.CartResponseDTO;
import com.minishop.minishopspring.dto.UpdateCartItemRequestDTO;
import com.minishop.minishopspring.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<CartItemResponseDTO> addToCart(
            @Valid @RequestBody AddCartItemRequestDTO request
    ) {

        CartItemResponseDTO response =
                cartService.addToCart(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart() {

        CartResponseDTO response =
                cartService.getCart();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<CartItemResponseDTO> updateQuantity(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCartItemRequestDTO request
    ) {

        CartItemResponseDTO response =
                cartService.updateQuantity(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> removeItem(
            @PathVariable Long id
    ) {

        cartService.removeItem(id);

        return ResponseEntity.noContent().build();
    }
}
