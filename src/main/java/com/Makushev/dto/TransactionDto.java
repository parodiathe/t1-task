package com.Makushev.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDto (
        Long id,
        BigDecimal amount,
        Long accountId,
        LocalDateTime time,
        String status,
        LocalDateTime timestamp,
        Long transactionId
) {
}
