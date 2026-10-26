package com.minishop.minishopspring.exception;

public class CartException extends RuntimeException {

    public CartException(String message) {
        super(message);
    }
}