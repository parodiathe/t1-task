package com.Makushev.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDto(
        Long clientId,
        Long accountId,
        Long transactionId,
        LocalDateTime timestamp,
        BigDecimal transactionAmount,
        BigDecimal accountBalance
) {}
