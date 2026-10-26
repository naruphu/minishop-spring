package com.minishop.minishopspring.controller;

import com.minishop.minishopspring.dto.OrderResponseDTO;
import com.minishop.minishopspring.dto.UpdateOrderStatusRequestDTO;
import com.minishop.minishopspring.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders() {

        return ResponseEntity.ok(
                orderService.getMyOrders()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getMyOrderById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                orderService.getMyOrderById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {

        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequestDTO request
    ) {
        OrderResponseDTO response =
                orderService.updateStatus(id, request);

        return ResponseEntity.ok(response);
    }
}
