package org.example.exception;

public class NoPhoneNumberLeftException extends ClientSideException {

    public NoPhoneNumberLeftException(Long userId, String phoneNumber) {
        super(
          String.format(
              "For user '%d' unable to remove phone number '%s' - no more phone numbers would left",
              userId,
              phoneNumber
          )
        );
    }
}
