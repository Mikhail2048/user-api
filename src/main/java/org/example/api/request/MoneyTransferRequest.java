package org.example.api.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class MoneyTransferRequest implements UserIdRequest {

    private Long fromUserId;

    private Long toUserId;

    private BigDecimal amount;

    @Override
    public Long getUserId() {
        return fromUserId;
    }
}