package com.minishop.minishopspring.controller;

import com.minishop.minishopspring.dto.OrderResponseDTO;
import com.minishop.minishopspring.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> checkout() {

        OrderResponseDTO response =
                checkoutService.checkout();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}