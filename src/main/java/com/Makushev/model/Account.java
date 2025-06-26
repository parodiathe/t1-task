package com.Makushev.model;

import com.Makushev.enums.AccountStatus;
import com.Makushev.enums.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Account type cannot be null")
    private AccountType accountType;

    @Column(nullable = false)
    @DecimalMin(value = "0.00", message = "Balance should not be below 0.00")
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private BigDecimal frozenAmount;

}
