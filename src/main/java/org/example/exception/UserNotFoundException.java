package org.example.exception;

public class UserNotFoundException extends ClientSideException {

    public UserNotFoundException(Long id) {
        super(String.format("User with id '%d' was not found", id));
    }

    public UserNotFoundException(String username) {
        super(String.format("User with username '%s' was not found", username));
    }

}
