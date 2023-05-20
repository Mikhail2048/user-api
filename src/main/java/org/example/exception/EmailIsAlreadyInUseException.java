package org.example.exception;

public class EmailIsAlreadyInUseException extends ClientSideException {

    public EmailIsAlreadyInUseException(String email) {
        super(String.format("email '%s' is already occupied", email));
    }
}
