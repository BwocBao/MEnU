package com.MEnU.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(long id) {
        super("Not found with id: " + id);
    }

    public NotFoundException(String message) {
        super(message);
    }
}