package com.Makushev.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionAcceptMessage(
        Long clientId,
        Long accountId,
        Long transactionId,
        LocalDateTime timestamp,
        BigDecimal amount,
        BigDecimal balance
) {}
