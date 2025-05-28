package com.Makushev.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @NotNull(message = "Account cannot be null")
    private Account account;

    @Column(nullable = false)
    @DecimalMin(value = "0.01", message = "Transaction amount cannot be less than 0")
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime transactionTime;

}
