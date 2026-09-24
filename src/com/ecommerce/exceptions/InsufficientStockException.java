package com.ecommerce.exceptions;

public class InsufficientStockException extends Exception {
    public InsufficientStockException() {
        super("Insufficient stock available.");
    }
    public InsufficientStockException(String message) {
        super(message);
    }
}
