package org.example.exception;

public class NoEmailLeftException extends ClientSideException {

    public NoEmailLeftException(Long userId, String email) {
        super(
          String.format(
            "For user '%d' unable to remove email '%s' - no more emails would left",
            userId,
            email
          )
        );
    }
}
