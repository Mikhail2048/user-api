package org.example.exception;

public class PhoneNumberIsAlreadyInUseException extends ClientSideException {

    public PhoneNumberIsAlreadyInUseException(String phoneNumber) {
        super(String.format("Phone number '%s' is already occupied", phoneNumber));
    }
}
