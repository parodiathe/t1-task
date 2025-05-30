package com.Makushev.controllers;

import com.Makushev.annotations.Cached;
import com.Makushev.annotations.Metric;
import com.Makushev.exception.TransactionException;
import com.Makushev.model.Transaction;
import com.Makushev.repository.TransactionRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody @Valid Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @GetMapping
    @Cached
    @Metric
    public List<Transaction> getAllTransactions() {
        System.out.println("Import from DB");
        return transactionRepository.findAll();
    }

    @GetMapping("/{id}")
    public Transaction getTransactionById(@PathVariable Long id) throws TransactionException {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionException("Transaction not found with id: " + id));
    }

}
