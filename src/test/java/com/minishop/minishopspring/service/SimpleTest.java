package com.minishop.minishopspring.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleTest {

    @Test
    void shouldCalculateSubtotal() {

        int price = 100;
        int quantity = 3;

        int subtotal = price * quantity;

        assertEquals(300, subtotal);
    }
}