package org.example.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(Long userId) {
        super(String.format("Not authorized to modify state of User : '%d'", userId));
    }
}