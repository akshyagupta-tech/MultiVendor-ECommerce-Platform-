package com.ecommerce.exceptions;

public class InvalidOrderException extends Exception {
    public InvalidOrderException() {
        super("Invalid order parameters.");
    }
    public InvalidOrderException(String message) {
        super(message);
    }
}
