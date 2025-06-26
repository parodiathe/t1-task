package com.Makushev.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProcessedTransactionDto(
        Long clientId,
        Long accountId,
        Long transactionId,
        LocalDateTime timestamp,
        BigDecimal transactionAmount,
        BigDecimal accountBalance
) {}
