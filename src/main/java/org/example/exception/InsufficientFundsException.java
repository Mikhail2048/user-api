package org.example.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends ClientSideException {

    public InsufficientFundsException(Long userId, BigDecimal amount) {
        super(String.format("User '%s' has insufficient amount of money to transfer '%d'", userId, amount.intValue()));
    }
}
